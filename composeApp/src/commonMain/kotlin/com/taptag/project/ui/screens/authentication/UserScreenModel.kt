package com.taptag.project.ui.screens.authentication

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.ChangePasswordRequestDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.UserProfileDomain
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.domain.repository.PreferenceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "UserScreenModel"

// Error constants to avoid hardcoded strings
private object ErrorCodes {
    const val SESSION_EXPIRED = "Session expired. Please login again."
    const val REFRESH_FAILED = "Failed to refresh session"
}

data class UserState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val currentUser: AuthResponseDomain? = null,
    val userProfile: UserProfileDomain = UserProfileDomain(),
    val isAuthenticated: Boolean = false,
    val navigateToSignIn: Boolean = false,
    val toggleLogOutDialog: Boolean = false,
    val showErrorDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val showSavedConfirmation: Boolean = false,
    val userExists: Boolean = false

)

class UserScreenModel(
    private val authRepository: AuthenticationRepository,
    private val preferenceRepository: PreferenceRepository
) : StateScreenModel<UserState>(initialState = UserState()) {

//    init {
//        println("$TAG: Initializing UserScreenModel")
//        validateSession()
//    }

    private fun updateState(update: (UserState) -> UserState) {
        mutableState.update { update(it) }
    }

    private fun setLoading(loading: Boolean) {
        println("$TAG: Setting loading state to $loading")
        updateState { it.copy(isLoading = loading) }
    }

    private fun handleError(message: String, showDialog: Boolean = true) {
        println("$TAG: Error: $message")
        updateState {
            it.copy(
                error = message,
                showErrorDialog = showDialog
            )
        }
    }

    fun toggleLogOutDialog(state: Boolean) {
        println("$TAG: Toggle logout dialog: $state")
        updateState { it.copy(toggleLogOutDialog = state) }
    }

    fun toggleErrorDialog(state: Boolean) {
        println("$TAG: Toggle error dialog: $state")
        updateState { it.copy(showErrorDialog = state) }
    }

    fun toggleIsEditMode(state: Boolean) {

        mutableState.update {
            it.copy(
                isEditMode = state
            )
        }
    }

    fun toggleShowSavedConfirmation(state: Boolean) {

        mutableState.update {
            it.copy(
                showSavedConfirmation = state
            )
        }
    }

    fun validateSession() {
        println("$TAG: Validating session")
        screenModelScope.launch {
            try {
                val accessToken = preferenceRepository.readAccessToken().firstOrNull()
                val refreshToken = preferenceRepository.readRefreshToken().firstOrNull()
                val userId = preferenceRepository.readUserId().firstOrNull()

                println("$TAG: validateSession() accessToken exists: ${!accessToken.isNullOrEmpty()}")
                println("$TAG: validateSession() refreshToken exists: ${!refreshToken.isNullOrEmpty()}")
                println("$TAG: validateSession() userId exists: ${!userId.isNullOrEmpty()}")

                if (accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    println("$TAG: Found refresh token but no access token, attempting to refresh")
                    refreshAccessToken(refreshToken)
                    return@launch
                }

                if (accessToken.isNullOrEmpty()) {
                    updateState { it.copy(isAuthenticated = false) }

                    println("$TAG: No access token found and no refresh possible, user not authenticated")
                    return@launch
                }

                if (userId.isNullOrEmpty()) {
                    println("$TAG: Found token but no userId, clearing inconsistent state")
                    clearSession()
                    updateState { it.copy(isAuthenticated = false) }

                    return@launch
                }

                val cachedUser = authRepository.getCachedUser(userId)
                when (cachedUser) {
                    is DataResult.Success -> {
                        if (cachedUser.data != null) {
                            // We have valid cached user data
                            println("$TAG: Found valid cached user data")
                            updateState {
                                it.copy(
                                    currentUser = cachedUser.data,
                                    isAuthenticated = true
                                )
                            }
                        } else {
                            println("$TAG: Cached user data is null")
                            handleSessionValidationFailure(refreshToken)
                        }
                    }

                    is DataResult.Error -> {
                        println("$TAG: Error retrieving cached user: ${cachedUser.message}")
                        handleSessionValidationFailure(refreshToken)
                    }
                }
            } catch (e: Exception) {
                println("$TAG: Exception in validateSession: ${e.message}")
                if (e !is CancellationException) {
                    handleError("Session validation failed: ${e.message}", false)
                }
            }
        }
    }

    private suspend fun handleSessionValidationFailure(refreshToken: String?) {
        if (!refreshToken.isNullOrEmpty()) {
            // Try to refresh the token
            println("$TAG: Attempting to refresh token")
            refreshAccessToken(refreshToken)
        } else {
            // No refresh token, can't restore session
            println("$TAG: No refresh token available, clearing session")
            clearSession()
            updateState { it.copy(isAuthenticated = false) }
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String) {
        try {
            println("$TAG: Refreshing access token")
            try {
                val result = authRepository.refreshToken(
                    RefreshTokenRequestDomain(refreshToken = refreshToken)
                )

                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Token refresh successful")
                        // Save the new tokens
                        preferenceRepository.saveAccessToken(result.data.accessToken)
                        preferenceRepository.saveRefreshToken(result.data.refreshToken)

                        // Get user data with the new token

                        screenModelScope.launch {
                            try {
                                val userId = preferenceRepository.readUserId().firstOrNull()
                                if (!userId.isNullOrEmpty()) {
                                    val cachedUser = authRepository.getCachedUser(userId)

                                    when (cachedUser) {
                                        is DataResult.Success -> {
                                            println("$TAG: Retrieved user after refresh: ${cachedUser.data?.user?.email}")
                                            updateState {
                                                it.copy(
                                                    currentUser = cachedUser.data,
                                                    isAuthenticated = true
                                                )
                                            }
                                        }

                                        is DataResult.Error -> {
                                            println("$TAG: Failed to get user after refresh: ${cachedUser.message}")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                println("$TAG: Exception getting user after refresh: ${e.message}")
                            }
                        }
                    }

                    is DataResult.Error -> {
                        // Refresh failed, session is invalid
                        println("$TAG: Token refresh failed: ${result.message}")
                        clearSession()
                        updateState {
                            it.copy(
                                isAuthenticated = false,
                                error = ErrorCodes.SESSION_EXPIRED,
                                showErrorDialog = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                println("$TAG: Exception during token refresh: ${e.message}")
                if (e !is CancellationException) {
                    clearSession()
                    updateState {
                        it.copy(
                            isAuthenticated = false,
                            error = "${ErrorCodes.REFRESH_FAILED}: ${e.message}",
                            showErrorDialog = true
                        )
                    }
                }
            }
        } catch (e: Exception) {
            println("$TAG: Mutex exception during refresh: ${e.message}")
        }
    }

    private suspend fun clearSession() {
        println("$TAG: Clearing user session")
        try {
            preferenceRepository.deleteAccessToken()
            preferenceRepository.deleteRefreshToken()
            preferenceRepository.deleteUserId()
        } catch (e: Exception) {
            println("$TAG: Exception while clearing session: ${e.message}")
        }
    }

    fun signUpUser(data: AuthRequestDomain) {
        println("$TAG: Signing up user: ${data.email}")
        screenModelScope.launch {
            try {
                setLoading(true)

                val result = authRepository.registerUser(data = data)

                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Sign up successful: ${result.data.user.email}")
                        updateState {
                            it.copy(
                                currentUser = result.data,
                                navigateToSignIn = true,
                                message = "Registration successful! Please sign in."
                            )
                        }


                        saveUserProfile(
                            token = preferenceRepository.readAccessToken().toString(),
                            data = UserProfileDomain(
                                firstName = data.firstName.toString(),
                                lastName = data.secondName.toString(),
                                email = data.email,
                                workEmail = data.workEmail,
                                company = data.company
                            )
                        )
                    }

                    is DataResult.Error -> {
                        println("$TAG: Sign up failed: ${result.message}")
                        handleError(result.message)
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                println("$TAG: Exception during sign up: ${e.message}")
                if (e !is CancellationException) {
                    e.message?.let { handleError(it) }
                    setLoading(false)
                }
            }
        }
    }

    fun signInUser(data: AuthRequestDomain) {
        println("$TAG: Signing in user: ${data.email}")
        screenModelScope.launch {
            try {
                setLoading(true)

                val result = authRepository.loginUser(data = data)

                println("the sign data is: $result")

                when (result) {
                    is DataResult.Success -> {
                        println("$TAG: Sign in successful: ${result.data}")

                        preferenceRepository.saveAccessToken(result.data.accessToken)
                        preferenceRepository.saveRefreshToken(result.data.refreshToken)
                        preferenceRepository.saveUserId(result.data.user.id)

                        println("$TAG: saved refresh token: ${preferenceRepository.readRefreshToken().firstOrNull()}")

                        delay(3000)

                        updateState {
                            it.copy(
                                currentUser = result.data,
                                isAuthenticated = true,
                                message = "Welcome back, ${result.data.user.email}!"
                            )
                        }
                    }

                    is DataResult.Error -> {
                        println("$TAG: Sign in failed: ${result.message}")
                        handleError(result.message)
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                println("$TAG: Exception during sign in: ${e.message}")
                if (e !is CancellationException) {
                    screenModelScope.cancel()
                    e.message?.let { handleError(it) }
                    setLoading(false)
                }
            }
        }
    }

    fun changePassword(data: ChangePasswordRequestDomain) {
        screenModelScope.launch {

            setLoading(true)

            val result = authRepository.changePassword(data)

            when (result) {
                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            message = "Password changed Successfully"
                        )
                    }

                    setLoading(false)
                }

                is DataResult.Error -> {
                    handleError(result.message)

                    setLoading(false)
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        screenModelScope.launch {

            setLoading(true)

            checkExistingUser(email)

            if (mutableState.value.userExists) {

                val result = authRepository.forgotPassword(email)

                when (result) {
                    is DataResult.Success -> {
                        mutableState.update {
                            it.copy(
                                message = "Password changed Successfully",
                                userExists = false
                            )
                        }

                        setLoading(false)
                    }

                    is DataResult.Error -> {
                        handleError(result.message)

                        setLoading(false)
                    }
                }
            } else {
                handleError("invalid email")
            }
        }
    }

    fun checkExistingUser(email: String) {
        screenModelScope.launch {

            setLoading(true)

            val result = authRepository.checkExistingUser(email)

            when (result) {
                is DataResult.Success -> {
                    setLoading(false)
                }

                is DataResult.Error -> {
                    handleError(result.message)

                    setLoading(false)
                }
            }
        }
    }

    fun saveUserProfile(
        token: String,
        data: UserProfileDomain
    ) {
        screenModelScope.launch {

            setLoading(true)

            val result = authRepository.saveUserProfile(data = data, token = token)

            when (result) {
                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            message = "profile saved!"
                        )
                    }

                    fetchUserProfile(token)

                    setLoading(false)
                }

                is DataResult.Error -> {
                    handleError(result.message)

                    setLoading(false)
                }
            }
        }
    }

    fun fetchUserProfile(token: String) {
        screenModelScope.launch {

            setLoading(true)

            val result = authRepository.fetchUserProfile(token = token)

            when (result) {
                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            userProfile = result.data
                        )
                    }

                    setLoading(false)
                }

                is DataResult.Error -> {
                    handleError(result.message)

                    setLoading(false)
                }
            }
        }
    }

    fun handleProfileUpdate(profile: UserProfileDomain) {
        mutableState.update {
            it.copy(
                userProfile = profile
            )
        }
    }

    fun updateUserProfile(
        id: String,
        data: UserProfileDomain
    ) {
        screenModelScope.launch {

            setLoading(true)

            val result = authRepository.updateUserProfile(id = id, data = data)

            when (result) {
                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            message = "Profile Updated!"
                        )
                    }

                    fetchUserProfile(
                        token = preferenceRepository.readAccessToken().firstOrNull().toString()
                    )

                    setLoading(false)
                }

                is DataResult.Error -> {
                    handleError(result.message)

                    setLoading(false)
                }
            }
        }
    }

    fun deleteUserProfile(id: String) {
        screenModelScope.launch {

            setLoading(true)

            val result = authRepository.deleteUserProfile(id = id)

            when (result) {
                is DataResult.Success -> {
                    mutableState.update {
                        it.copy(
                            message = "Profile Deleted Successfully"
                        )
                    }

                    setLoading(false)
                }

                is DataResult.Error -> {
                    handleError(result.message)

                    setLoading(false)
                }
            }
        }
    }


    fun logoutUser() {
        println("$TAG: Logging out user")
        // Use authScope to prevent cancellation issues
        screenModelScope.launch {
            try {
                setLoading(true)

                val userId = preferenceRepository.readUserId().firstOrNull() ?: ""
                val accessToken = preferenceRepository.readAccessToken().firstOrNull() ?: ""

                println("$TAG: Logout - userId: $userId, has token: ${accessToken.isNotEmpty()}")

                // Call logout API if we have a token
                if (accessToken.isNotEmpty()) {
                    println("$TAG: Calling logout API")
                    try {
                        val logoutResult = authRepository.logoutUser(accessToken)
                        when (logoutResult) {
                            is DataResult.Success -> println("$TAG: Logout API successful")
                            is DataResult.Error -> println("$TAG: Logout API failed: ${logoutResult.message}")
                        }
                    } catch (e: Exception) {
                        println("$TAG: Exception calling logout API: ${e.message}")
                    }
                }

                // Clear cached user data if we have a user ID
                if (userId.isNotEmpty()) {
                    println("$TAG: Clearing user data for: $userId")
                    try {
                        authRepository.clearUserData(userId)
                    } catch (e: Exception) {
                        println("$TAG: Exception clearing user data: ${e.message}")
                    }
                }

                // Always clear local session data
                println("$TAG: Finalizing logout by clearing session data")
                clearSession()

                updateState {
                    it.copy(
                        isAuthenticated = false,
                        currentUser = null,
                        toggleLogOutDialog = false
                    )
                }
                setLoading(false)

                println("$TAG: Logout complete")
            } catch (e: Exception) {
                println("$TAG: Exception during logout: ${e.message}")
                if (e !is CancellationException) {
                    setLoading(false)
                }
            }
        }
    }
}
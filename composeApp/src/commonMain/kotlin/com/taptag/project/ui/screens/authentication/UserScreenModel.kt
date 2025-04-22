package com.taptag.project.ui.screens.authentication

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.domain.repository.PreferenceRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val TAG = "UserScreenModel"

// Error constants to avoid hardcoded strings
private object ErrorCodes {
    const val TOKEN_INVALID = "token_invalid"
    const val UNAUTHENTICATED = "unauthenticated"
    const val SESSION_EXPIRED = "Session expired. Please login again."
    const val NO_AUTH_TOKEN = "No authentication token found"
    const val NO_REFRESH_TOKEN = "No refresh token found"
    const val REFRESH_FAILED = "Failed to refresh session"
}

data class UserState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val currentUser: AuthResponseDomain? = null,
    val isAuthenticated: Boolean = false,
    val navigateToSignIn: Boolean = false,
    val toggleLogOutDialog: Boolean = false,
    val showErrorDialog: Boolean = false
)

class UserScreenModel(
    private val authRepository: AuthenticationRepository,
    private val preferenceRepository: PreferenceRepository
) : StateScreenModel<UserState>(initialState = UserState()) {

    // Mutex to prevent concurrent token refresh operations
    private val refreshTokenMutex = Mutex()

    // A separate coroutine scope with SupervisorJob for auth operations that shouldn't be canceled together
    private val authScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        println("$TAG: Initializing UserScreenModel")
        validateSession()
    }

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

    fun checkAuthentication() {
        println("$TAG: Checking authentication")
        screenModelScope.launch {
            try {
                val token = preferenceRepository.readAccessToken().firstOrNull()
                val userId = preferenceRepository.readUserId().firstOrNull()

                println("$TAG: checkAuthentication() token exists: ${!token.isNullOrEmpty()}")

                if (!token.isNullOrEmpty()) {
                    println("$TAG: checkAuthentication() user id: $userId")

                    if (!userId.isNullOrEmpty()) {
                        val cachedUser = authRepository.getCachedUser(userId)

                        when (cachedUser) {
                            is DataResult.Success -> {
                                println("$TAG: Successfully retrieved cached user: ${cachedUser.data?.user?.email}")
                                updateState {
                                    it.copy(
                                        currentUser = cachedUser.data,
                                        isAuthenticated = true
                                    )
                                }
                            }
                            is DataResult.Error -> {
                                println("$TAG: Failed to get cached user: ${cachedUser.message}")
                            }
                        }
                    } else {
                        println("$TAG: Found token but no userId, clearing inconsistent state")
                        clearSession()
                    }
                } else {
                    println("$TAG: No authentication token found")
                }
            } catch (e: Exception) {
                println("$TAG: Exception in checkAuthentication: ${e.message}")
                if (e !is CancellationException) {
                    handleError("Failed to check authentication: ${e.message}", false)
                }
            }
        }
    }

    fun validateSession() {
        println("$TAG: Validating session")
        // Using authScope for session validation to prevent cancellation issues
        authScope.launch {
            try {
                val accessToken = preferenceRepository.readAccessToken().firstOrNull()
                val refreshToken = preferenceRepository.readRefreshToken().firstOrNull()
                val userId = preferenceRepository.readUserId().firstOrNull()

                println("$TAG: validateSession() accessToken exists: ${!accessToken.isNullOrEmpty()}")
                println("$TAG: validateSession() refreshToken exists: ${!refreshToken.isNullOrEmpty()}")
                println("$TAG: validateSession() userId exists: ${!userId.isNullOrEmpty()}")

                // If we have a refresh token but no access token, try to refresh immediately
                if (accessToken.isNullOrEmpty() && !refreshToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    println("$TAG: Found refresh token but no access token, attempting to refresh")
                    refreshAccessToken(refreshToken)
                    return@launch
                }

                if (accessToken.isNullOrEmpty()) {
                    // No token and no refresh possibility, user is definitely not authenticated
                    withContext(Dispatchers.Main) {
                        updateState { it.copy(isAuthenticated = false) }
                    }
                    println("$TAG: No access token found and no refresh possible, user not authenticated")
                    return@launch
                }

                if (userId.isNullOrEmpty()) {
                    // We have a token but no user ID, clear inconsistent state
                    println("$TAG: Found token but no userId, clearing inconsistent state")
                    clearSession()
                    withContext(Dispatchers.Main) {
                        updateState { it.copy(isAuthenticated = false) }
                    }
                    return@launch
                }

                val cachedUser = authRepository.getCachedUser(userId)
                when (cachedUser) {
                    is DataResult.Success -> {
                        if (cachedUser.data != null) {
                            // We have valid cached user data
                            println("$TAG: Found valid cached user data")
                            withContext(Dispatchers.Main) {
                                updateState {
                                    it.copy(
                                        currentUser = cachedUser.data,
                                        isAuthenticated = true
                                    )
                                }
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
                    withContext(Dispatchers.Main) {
                        handleError("Session validation failed: ${e.message}", false)
                    }
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
            withContext(Dispatchers.Main) {
                updateState { it.copy(isAuthenticated = false) }
            }
        }
    }

    private suspend fun refreshAccessToken(refreshToken: String) {
        // Use mutex to prevent concurrent refresh attempts
        try {
            refreshTokenMutex.withLock {
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
                            withContext(Dispatchers.Main) {
                                // Using a new coroutine to prevent cancellation issues
                                authScope.launch {
                                    try {
                                        val userId = preferenceRepository.readUserId().firstOrNull()
                                        if (!userId.isNullOrEmpty()) {
                                            val cachedUser = authRepository.getCachedUser(userId)

                                            when (cachedUser) {
                                                is DataResult.Success -> {
                                                    println("$TAG: Retrieved user after refresh: ${cachedUser.data?.user?.email}")
                                                    withContext(Dispatchers.Main) {
                                                        updateState {
                                                            it.copy(
                                                                currentUser = cachedUser.data,
                                                                isAuthenticated = true
                                                            )
                                                        }
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
                        }
                        is DataResult.Error -> {
                            // Refresh failed, session is invalid
                            println("$TAG: Token refresh failed: ${result.message}")
                            clearSession()
                            withContext(Dispatchers.Main) {
                                updateState {
                                    it.copy(
                                        isAuthenticated = false,
                                        error = ErrorCodes.SESSION_EXPIRED,
                                        showErrorDialog = true
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("$TAG: Exception during token refresh: ${e.message}")
                    if (e !is CancellationException) {
                        clearSession()
                        withContext(Dispatchers.Main) {
                            updateState {
                                it.copy(
                                    isAuthenticated = false,
                                    error = "${ErrorCodes.REFRESH_FAILED}: ${e.message}",
                                    showErrorDialog = true
                                )
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            println("$TAG: Mutex exception during refresh: ${e.message}")
        }
    }

    // More robust error detection
    private fun isTokenError(errorMessage: String): Boolean {
        val tokenErrorPhrases = listOf(
            "token is not valid",
            "token invalid",
            "user not authenticated",
            "unauthorized",
            "unauthenticated",
            "expired token",
            "jwt expired"
        )
        return tokenErrorPhrases.any { errorMessage.contains(it, ignoreCase = true) }
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
        // Use authScope to prevent cancellation issues
        authScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    setLoading(true)
                }

                val result = authRepository.registerUser(data = data)

                withContext(Dispatchers.Main) {
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
                        }
                        is DataResult.Error -> {
                            println("$TAG: Sign up failed: ${result.message}")
                            handleError(result.message)
                        }
                    }
                    setLoading(false)
                }
            } catch (e: Exception) {
                println("$TAG: Exception during sign up: ${e.message}")
                if (e !is CancellationException) {
                    withContext(Dispatchers.Main) {
                        e.message?.let { handleError(it) }
                        setLoading(false)
                    }
                }
            }
        }
    }

    fun signInUser(data: AuthRequestDomain) {
        println("$TAG: Signing in user: ${data.email}")
        // Use authScope to prevent cancellation issues
        authScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    setLoading(true)
                }

                val result = authRepository.loginUser(data = data)

                withContext(Dispatchers.Main) {
                    when (result) {
                        is DataResult.Success -> {
                            println("$TAG: Sign in successful: ${result.data.user.email}")

                            preferenceRepository.saveAccessToken(result.data.accessToken)
                            preferenceRepository.saveRefreshToken(result.data.refreshToken)
                            preferenceRepository.saveUserId(result.data.user.id)

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
                }
            } catch (e: Exception) {
                println("$TAG: Exception during sign in: ${e.message}")
                if (e !is CancellationException) {
                    withContext(Dispatchers.Main) {
                        e.message?.let { handleError(it) }
                        setLoading(false)
                    }
                }
            }
        }
    }

    fun logoutUser() {
        println("$TAG: Logging out user")
        // Use authScope to prevent cancellation issues
        authScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    setLoading(true)
                }

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

                withContext(Dispatchers.Main) {
                    updateState {
                        it.copy(
                            isAuthenticated = false,
                            currentUser = null,
                            toggleLogOutDialog = false
                        )
                    }
                    setLoading(false)
                }

                println("$TAG: Logout complete")
            } catch (e: Exception) {
                println("$TAG: Exception during logout: ${e.message}")
                if (e !is CancellationException) {
                    withContext(Dispatchers.Main) {
                        setLoading(false)
                    }
                }
            }
        }
    }
}
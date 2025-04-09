package com.taptag.project.ui.screens.authentication

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private fun isLoading(loading: Boolean) {
        mutableState.value = state.value.copy(
            isLoading = loading
        )
    }

    private fun handleError(message: String) {
        mutableState.value = state.value.copy(
            error = message
        )
    }

    fun toggleLogOutDialog(state: Boolean) {

        mutableState.update {
            it.copy(
                toggleLogOutDialog = state
            )
        }
    }

    fun toggleErrorDialog(state: Boolean) {

        mutableState.update {
            it.copy(
                showErrorDialog = state
            )
        }
    }

    fun signUpUser(data: AuthRequestDomain) {
        screenModelScope.launch {
            isLoading(true)

            val result = authRepository.registerUser(data = data)

            try {
                when (result) {
                    is DataResult.Success -> {

                        mutableState.value = state.value.copy(
                            currentUser = result.data,
                            navigateToSignIn = true
                        )
                        println("sign up successful: ${result.data}")

                    }

                    is DataResult.Error -> {

                        handleError(result.message)

                        println("sign up failed: ${result.message}")
                        println("screen model register output: $result")

                    }

                }

                isLoading(false)

            } catch (e: Exception) {
                e.message?.let { handleError(it) }
                println("sign up failed: ${e.message}")
                isLoading(false)
            }
        }
    }

    fun signInUser(data: AuthRequestDomain) {
        screenModelScope.launch {

            isLoading(true)

            try {
                when (val result = authRepository.loginUser(data = data)) {
                    is DataResult.Success -> {

                        preferenceRepository.saveAccessToken(result.data.accessToken)

                        preferenceRepository.saveRefreshToken(result.data.refreshToken)

                        mutableState.value = state.value.copy(
                            currentUser = result.data,
                            isAuthenticated = true
                        )

                    }

                    is DataResult.Error -> {

                        handleError(result.message)

                    }

                }

                isLoading(false)

            } catch (e: Exception) {
                e.message?.let { handleError(it) }

                isLoading(false)
            }
        }
    }
}
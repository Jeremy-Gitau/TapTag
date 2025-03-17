package com.taptag.project.ui.screens.authentication

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.CurrentUserDomain
import com.taptag.project.domain.models.UserRequestDomain
import com.taptag.project.domain.models.UserResponseDomain
import com.taptag.project.domain.repository.AuthenticationRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val currentUser: UserResponseDomain? = null,
    val isAuthenticated: Boolean = false,
    val navigateToSignIn: Boolean = false,
    val toggleLogOutDialog: Boolean = false
)

class AuthenticationScreenModel(
    private val authRepository: AuthenticationRepository
) : StateScreenModel<AuthState>(initialState = AuthState()) {

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

    fun toggleLogOutDialog(state: Boolean){

        mutableState.update {
            it.copy(
                toggleLogOutDialog = state
            )
        }
    }

    fun signUpUser(data: UserRequestDomain) {
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

    fun signInUser(data: UserRequestDomain) {
        screenModelScope.launch {
            isLoading(true)

            val result = authRepository.loginUser(data = data)

            try {
                when (result) {
                    is DataResult.Success -> {

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
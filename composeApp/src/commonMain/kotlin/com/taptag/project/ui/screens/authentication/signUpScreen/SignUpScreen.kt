package com.taptag.project.ui.screens.authentication.signUpScreen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.common.ErrorDialog
import com.taptag.project.ui.common.LoadingIndicator
import com.taptag.project.ui.composables.authentication.signUp.SignUpContent
import com.taptag.project.ui.screens.authentication.UserScreenModel
import com.taptag.project.ui.screens.authentication.signInScreen.SignInScreen

@OptIn(ExperimentalMaterial3Api::class)
class SignUpScreen : Screen {

    @Composable
    override fun Content() {

        val userScreenModel: UserScreenModel = koinScreenModel()
        val userState by userScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(userState.error){
            userScreenModel.toggleErrorDialog(true)
        }

        when {
            userState.isLoading -> {
                LoadingIndicator()
            }

            userState.showErrorDialog -> {

                ErrorDialog(
                    message = userState.error,
                    onDismiss = {userScreenModel.toggleErrorDialog(false)},
                    onRetry = null
                )
            }

        }

        if (userState.navigateToSignIn) {

            navigator.push(SignInScreen())

            println("${userState.currentUser}")

        }

        SignUpContent(
            onClickSignUp = userScreenModel::signUpUser,
            state = userState,
            navigate = navigator,
            userScreenModel = userScreenModel
        )
    }
}
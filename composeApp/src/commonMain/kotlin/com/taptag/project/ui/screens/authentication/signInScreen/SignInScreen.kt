package com.taptag.project.ui.screens.authentication.signInScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.common.ErrorDialog
import com.taptag.project.ui.common.LoadingIndicator
import com.taptag.project.ui.composables.authentication.signIn.SignInContent
import com.taptag.project.ui.screens.authentication.UserScreenModel
import com.taptag.project.ui.screens.home.HomeScreen

class SignInScreen() : Screen {

    @Composable
    override fun Content() {

        val userScreenModel: UserScreenModel = koinScreenModel()
        val userState by userScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            userScreenModel.validateSession()
        }

        LaunchedEffect(userState.isAuthenticated) {
            if (userState.isAuthenticated) {
                navigator.push(HomeScreen())
            }
        }

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

        SignInContent(
            onSignIn = userScreenModel::signInUser,
            navigate = navigator,
            userState = userState
        )

    }
}
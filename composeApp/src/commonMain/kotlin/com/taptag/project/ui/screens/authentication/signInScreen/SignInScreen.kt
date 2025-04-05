package com.taptag.project.ui.screens.authentication.signInScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.composables.authentication.signIn.SignInContent
import com.taptag.project.ui.screens.authentication.AuthenticationScreenModel
import com.taptag.project.ui.screens.home.HomeScreen

class SignInScreen() : Screen {

    @Composable
    override fun Content() {

        val authenticationScreenModel: AuthenticationScreenModel = koinScreenModel()
        val authState by authenticationScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        if(authState.isAuthenticated) {

            navigator.push(HomeScreen())
        }

        SignInContent(
            onSignIn = authenticationScreenModel::signInUser,
            navigate = navigator
        )
    }
}
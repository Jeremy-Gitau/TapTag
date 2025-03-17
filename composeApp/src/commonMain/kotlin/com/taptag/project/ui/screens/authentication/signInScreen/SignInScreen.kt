package com.taptag.project.ui.screens.authentication.signInScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.composables.authentication.signIn.SignInContent
import com.taptag.project.ui.screens.authentication.AuthenticationScreenModel

class SignInScreen(): Screen {

    @Composable
    override fun Content() {

        val authenticationScreenModel: AuthenticationScreenModel = koinScreenModel()
        val authState by authenticationScreenModel.state.collectAsState()

        val navigate = LocalNavigator.currentOrThrow

        SignInContent(
            onSignIn = authenticationScreenModel::signInUser,
            navigate = navigate
        )
    }
}
package com.taptag.project.ui.screens.settings

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
import com.taptag.project.ui.composables.settings.UserSettingsScreenContent
import com.taptag.project.ui.screens.authentication.UserScreenModel

class UserSettingsScreen: Screen {

    @Composable
    override fun Content() {

        val userScreenModel: UserScreenModel = koinScreenModel()
        val userState by userScreenModel.state.collectAsState()
        val settingsScreenModel: SettingsScreenModel = koinScreenModel()
        val settingsState by settingsScreenModel.state.collectAsState()

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

        UserSettingsScreenContent(
            navigator = navigator,
            settingsState = settingsState,
            userState = userState,
            settingsScreenModel = settingsScreenModel,
            onClickLogOut = userScreenModel::logoutUser,
            onDismiss = {},
            authenticationScreenModel = userScreenModel
        )
    }
}
package com.taptag.project.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.composables.settings.UserSettingsScreenContent
import com.taptag.project.ui.screens.authentication.UserScreenModel

class UserSettingsScreen: Screen {

    @Composable
    override fun Content() {

        val authenticationScreenModel: UserScreenModel = koinScreenModel()
        val authState by authenticationScreenModel.state.collectAsState()
        val settingsScreenModel: SettingsScreenModel = koinScreenModel()
        val settingsState by settingsScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        UserSettingsScreenContent(
            navigator = navigator,
            settingsState = settingsState,
            userState = authState,
            settingsScreenModel = settingsScreenModel,
            onClickLogOut = {},
            onDismiss = authenticationScreenModel::toggleLogOutDialog
        )
    }
}
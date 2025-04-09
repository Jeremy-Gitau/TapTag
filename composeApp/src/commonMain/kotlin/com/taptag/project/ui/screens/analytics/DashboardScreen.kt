package com.taptag.project.ui.screens.analytics

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
import com.taptag.project.ui.composables.analytics.DashboardScreenContent
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.authentication.UserScreenModel
import com.taptag.project.ui.screens.contact.ContactScreenModel
import com.taptag.project.ui.screens.settings.SettingsScreenModel

class DashboardScreen : Screen {

    @Composable
    override fun Content() {
        // Get view models via Koin
        val contactScreenModel: ContactScreenModel = koinScreenModel()
        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val settingsScreenModel: SettingsScreenModel = koinScreenModel()
        val userScreenModel: UserScreenModel = koinScreenModel()

        // Collect states
        val contactState by contactScreenModel.state.collectAsState()
        val nfcState by nfcScreenModel.state.collectAsState()
        val settingsState by settingsScreenModel.state.collectAsState()
        val userState by userScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(contactState.error) {
            if (contactState.error != null) {
                contactScreenModel.toggleShowErrorDialog(true)
            }
        }

        LaunchedEffect(settingsState.accessToken) {

            contactScreenModel.fetchAllContacts()

        }

        // Error dialog
        if (contactState.showErrorDialog) {
            ErrorDialog(
                message = contactState.error,
                onDismiss = {
                    contactScreenModel.toggleShowErrorDialog(false)
                },
                onRetry = {
                    contactScreenModel.toggleShowErrorDialog(false)
                    contactScreenModel.fetchAllContacts()
                }
            )
        }

        // Screen content based on state
        when {
            contactState.isLoading -> {
                LoadingIndicator()
            }

            else -> {
                DashboardScreenContent(
                    contactState = contactState,
                    onContactExpandToggled = contactScreenModel::toggleContactExpand,
                    navigator = navigator,
                    nfcScreenModel = nfcScreenModel,
                    token = settingsState.accessToken.toString(),
                    currentUser = userState.currentUser
                )
            }
        }
    }
}
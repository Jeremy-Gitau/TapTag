package com.taptag.project.ui.screens.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.composables.analytics.AnalyticsScreenContent
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.contact.ContactScreenModel
import com.taptag.project.ui.screens.settings.SettingsScreenModel

class AnalyticsScreen() : Screen {

    @Composable
    override fun Content() {

        val contactScreenModel: ContactScreenModel = koinScreenModel()
        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val settingsScreenModel: SettingsScreenModel = koinScreenModel()

        val contactState by contactScreenModel.state.collectAsState()
        val nfcState by nfcScreenModel.state.collectAsState()
        val settingsState by settingsScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(contactState.contacts, settingsState.accessToken, settingsState.isDarkMode) {


            contactScreenModel.getAllContacts(token = settingsState.accessToken.toString())

            settingsScreenModel.observeDarkMode()

        }

        if (contactState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else if (contactState.error != null) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contactState.error ?: "Unknown error occurred"
                )
            }

        } else {
            AnalyticsScreenContent(
                contactState = contactState,
                onContactExpandToggled = contactScreenModel::toggleContactExpand,
                navigator = navigator,
                nfcScreenModel = nfcScreenModel,
                token = settingsState.accessToken.toString()
            )
        }
    }
}
package com.taptag.project.ui.screens.analytics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
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

class AnalyticsScreen : Screen {

    @Composable
    override fun Content() {

        val contactScreenModel: ContactScreenModel = koinScreenModel()
        val nfcScreenModel: NFCScreenModel = koinScreenModel()

        val contactState by contactScreenModel.state.collectAsState()
        val nfcState by nfcScreenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(contactState.contacts) {

            contactScreenModel.getAllContacts()

        }

        if (contactState.isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {

                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
//            else if (contactState.error != null) {
//
//                Text(
//                    text = contactState.error ?: "Unknown error occurred",
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
        else {
            AnalyticsScreenContent(
                contactState = contactState,
                onContactExpandToggled = contactScreenModel::toggleContactExpand,
                navigator = navigator,
                nfcScreenModel = nfcScreenModel

            )
        }
    }
}
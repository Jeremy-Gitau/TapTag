package com.taptag.project.ui.screens.NFCScreen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.ui.composables.nfc.ErrorContent
import com.taptag.project.ui.composables.nfc.NFCReadScreenContent
import com.taptag.project.ui.screens.contact.ContactScreenModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NFCReadScreen : Screen {

    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val nfcManager = getNFCManager()

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val state by nfcScreenModel.state.collectAsState()

        val contactScreenModel: ContactScreenModel = koinScreenModel()

        val infiniteTransition = rememberInfiniteTransition(label = "")

        // Pulse animation for the border
        val pulseAnimation = infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )

        // Icon opacity animation
        val iconOpacity = infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        // Listen for NFC tag detection
        scope.launch {
            nfcManager.contacts.collectLatest { tagData ->
                println("Test: I have detected a tag  $tagData")
                if (!state.isWriteMode) {
                    // Regular read mode
                    nfcScreenModel.readTagContact(tagData)
                    nfcScreenModel.readTag(tagData.toString())
                    nfcScreenModel.toggleResultDialog(true)
//                    contactScreenModel.saveContact(contact = tagData)
                }
            }
        }



        if (state.showErrorDialog) {
            ErrorContent(
                errorMessage = state.nfcResult.toString(),
                onTryAgain = {},
                onDismiss = {
                    nfcScreenModel.toggleErrorDialog(false)
                }
            )
        }

        if (state.isScanning) {
            println("scanning for nfc")
            nfcManager.RegisterApp()
        }
        NFCReadScreenContent(
            nfcScreenModel = nfcScreenModel,
            contactScreenModel = contactScreenModel,
            state = state,
            isScanning = state.isScanning,
            onClickStartScanning = {
                nfcScreenModel.isScanning(true)
            },
            pulseAnimation = pulseAnimation,
            iconOpacity = iconOpacity
        )
    }
}

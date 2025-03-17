package com.taptag.project.ui.screens.NFCScreen

import android.annotation.SuppressLint
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.ui.composables.nfc.ErrorContent
import com.taptag.project.ui.composables.nfc.NFCScreenContent
import com.taptag.project.ui.composables.nfc.SuccessContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NFCScreen : Screen {

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val nfcManager = getNFCManager()

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val state by nfcScreenModel.state.collectAsState()

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

        scope.launch {
            nfcManager.tags.collectLatest { tagData ->
                println("Test: I have detected a tag  $tagData")
                nfcScreenModel.readTag(tagData)
                nfcScreenModel.toggleResultDialog(true)
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

        if (state.showResultDialog){
            state.nfcResult?.let {
                SuccessContent(nfcData = it) {

                }
            }
        }
        if (state.isScanning) {
            println("scanning for nfc")
            nfcManager.RegisterApp()
        }

        NFCScreenContent(
            nfcScreenModel = nfcScreenModel,
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
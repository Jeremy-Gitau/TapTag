package com.taptag.project.ui.screens.NFCScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.ui.composables.nfc.WriteTagDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ReadNfcScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val state by nfcScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        val nfcManager = getNFCManager()

        nfcManager.RegisterApp()

        scope.launch {
            nfcManager.tags.collectLatest { tagData ->
                println("I have detected a tag $tagData")
                nfcScreenModel.readTag(tagData)
            }
        }

        // Listen for write results
        scope.launch {
            nfcManager.writeResult.collectLatest { success ->
                nfcScreenModel.toggleWriteStatus(
                    if (success) {
                        "Tag written successfully!"
                    } else {
                        "Failed to write to tag. Please try again."
                    }
                )
                nfcScreenModel.toggleIsWriteMode(!success) // Stay in write mode if failed
                if (success) {
                    // Reset UI after successful write
                    nfcScreenModel.toggleShowWriteDialog(false)
                    nfcScreenModel.toggleResultDialog(true)
                    nfcScreenModel.isSuccess(success,"Tag written successfully with custom data")
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "back arrow from read nfc screen"
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { nfcScreenModel.toggleShowWriteDialog(true) }
                        ) {
                            Text(text = "edit")
                        }
                    }
                )
            }
        ) { padding ->

            // Show write dialog when requested
            if (state.showWriteDialog) {
                WriteTagDialog(
                    isWriteMode = state.isWriteMode,
                    writeStatus = state.writeStatus,
                    onWrite = { data ->
                        nfcScreenModel.toggleWriteStatus("Ready to write. Please tap NFC tag...")
                        println("data to be written is: $data")
                        nfcScreenModel.toggleIsWriteMode(true)
                        nfcManager.prepareWrite(data)
                    },
                    onCancel = {
                        if (state.isWriteMode) {
                            nfcManager.cancelWrite()
                        }
                        nfcScreenModel.toggleIsWriteMode(false)
                        nfcScreenModel.toggleShowWriteDialog(false)
                    }
                )
            }

            state.nfcResult?.let {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = it)
                }
            }
        }

    }
}
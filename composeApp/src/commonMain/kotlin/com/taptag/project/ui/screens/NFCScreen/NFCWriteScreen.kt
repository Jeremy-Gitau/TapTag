@file:OptIn(ExperimentalMaterial3Api::class)

package com.taptag.project.ui.screens.NFCScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.ui.composables.nfc.write.ContactInputForm
import com.taptag.project.ui.screens.contact.ContactScreenModel
import kotlinx.coroutines.launch

class NFCWriteScreen : Screen {

    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val nfcState by nfcScreenModel.state.collectAsState()

        val contactScreenModel: ContactScreenModel = koinScreenModel()

        val navigator = LocalNavigator.currentOrThrow


        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Write To NFC",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { paddingValue ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValue)
                    .padding(16.dp)
            ) {

                ContactInputForm(
                    contact = nfcState.currentContact,
                    onContactChange = { nfcScreenModel.observeCurrentContact(it) },
                    nfcScreenModel = nfcScreenModel,
                    contactScreenModel = contactScreenModel,
                    navigator = navigator,
                    scope = scope
                )

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }
}
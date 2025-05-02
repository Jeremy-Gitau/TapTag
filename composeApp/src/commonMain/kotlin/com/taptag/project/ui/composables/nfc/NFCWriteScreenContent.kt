package com.taptag.project.ui.composables.nfc

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.contact.ContactScreen
import com.taptag.project.ui.screens.contact.ContactScreenModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)

class NFCWriteScreenContent() : Screen {

    @Composable
    override fun Content() {

        val scope = rememberCoroutineScope()
        val nfcManager = getNFCManager()

        nfcManager.RegisterApp()

        val navigator = LocalNavigator.currentOrThrow

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val nfcState by nfcScreenModel.state.collectAsState()

        // Animation for pulsing border
        val infiniteTransition = rememberInfiniteTransition(label = "nfc-border-pulse")
        val borderWidth by infiniteTransition.animateFloat(
            initialValue = 4f,
            targetValue = 8f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "border-pulse"
        )

        // Color animation for the border
        val borderColor by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "border-color"
        )

        // Collect NFC scan results
        scope.launch {
            nfcManager.contacts.collect { contact ->
                nfcScreenModel.observeLastScannedContact(contact)
                nfcScreenModel.toggleIsWriteMode(false)
                nfcScreenModel.toggleWriteResult(null)
            }
        }

        // Collect write results
        scope.launch {
            nfcManager.writeResult.collect { success ->

                nfcScreenModel.toggleWriteStatus(
                    if (success) {
                        "Tag written successfully!"
                    } else {
                        "Failed to write to tag. Please try again."
                    }
                )
                nfcScreenModel.toggleIsWriteMode(!success) // Stay in write mode if failed
                if (success) {
                    nfcScreenModel.isSuccess(success, "Tag written successfully with custom data")
                }

                nfcScreenModel.toggleResultDialog(true)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            if (nfcState.isWriteMode) "Writing to NFC Tag..." else "Write NFC Tag",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() },
                            enabled = !nfcState.isWriteMode
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = null,
                                tint = if (nfcState.isWriteMode)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->

            if (nfcState.showResultDialog) {
                AlertDialog(
                    onDismissRequest = { nfcScreenModel.toggleResultDialog(false) },
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = if (nfcState.isSuccess) Icons.Filled.Check else Icons.Filled.Error,
                                contentDescription = null,
                                tint = if (nfcState.isSuccess) MaterialTheme.colorScheme.primary else Color.Red,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(bottom = 8.dp)
                            )
                            Text(
                                text = if (nfcState.isSuccess) "Success!" else "Error",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    text = {
                        Text(
                            text = nfcState.writeStatus,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                nfcScreenModel.toggleResultDialog(false)
                                if (nfcState.isSuccess) {
                                    // Go back twice to return to the main screen if successful
                                    navigator.pop()
                                    navigator.pop()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (nfcState.isSuccess)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(if (nfcState.isSuccess) "Done" else "OK")
                        }
                    },
                    dismissButton = {
                        if (!nfcState.isSuccess) {
                            TextButton(
                                onClick = {
                                    nfcScreenModel.toggleResultDialog(false)
                                    // Prepare to try writing again
                                    scope.launch {
                                        nfcManager.prepareWriteContact(nfcState.currentContact)
                                    }
                                }
                            ) {
                                Text("Try Again")
                            }
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(200.dp)
                ) {
                    // Calculate animated border color
                    val animatedColor = if (nfcState.isWriteMode) {
                        // Interpolate between primary and secondary colors during animation
                        Color(
                            red = MaterialTheme.colorScheme.primary.red * (1 - borderColor) +
                                    MaterialTheme.colorScheme.secondary.red * borderColor,
                            green = MaterialTheme.colorScheme.primary.green * (1 - borderColor) +
                                    MaterialTheme.colorScheme.secondary.green * borderColor,
                            blue = MaterialTheme.colorScheme.primary.blue * (1 - borderColor) +
                                    MaterialTheme.colorScheme.secondary.blue * borderColor,
                            alpha = 1f
                        )
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(CircleShape)
                            .background(
                                if (nfcState.isWriteMode)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            )
                            .border(
                                width = if (nfcState.isWriteMode) borderWidth.dp else 4.dp,
                                color = animatedColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Nfc,
                            contentDescription = null,
                            modifier = Modifier
                                .size(80.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = if (nfcState.isWriteMode) "Writing to NFC Tag..." else "Write to NFC Tag",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = if (nfcState.isWriteMode)
                        "Please hold your phone near the NFC tag"
                    else
                        "Create or edit information on an NFC tag",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                nfcState.currentContact.let { contact ->

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Contact Information Summary",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        ContactDetailItem("Name", contact.name)
                        ContactDetailItem("Email", contact.email)
                        ContactDetailItem("Role", contact.role)
                        ContactDetailItem("Company", contact.company)
                        ContactDetailItem("Status", contact.status.getLabel())

                        if (contact.tags.isNotEmpty()) {
                            Text(
                                text = "Tags",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp)
                            )

                        }

                        if (contact.notes.isNotEmpty()) {
                            Text(
                                text = "Notes",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(text = contact.notes)
                        }
                    }
                }

                Button(
                    onClick = {
                        // Set isWriteMode to true when button is clicked, which starts the animation
                        nfcScreenModel.toggleIsWriteMode(true)
                        scope.launch {
                            nfcManager.prepareWriteContact(nfcState.currentContact)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    enabled = !nfcState.isWriteMode
                ) {
                    Text(if (nfcState.isWriteMode) "Writing..." else "Write to Tag")
                }
            }
        }
    }
}

@Composable
fun ContactDetailItem(label: String, value: String) {
    if (value.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 2.dp)) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp
            )
        }
    }
}
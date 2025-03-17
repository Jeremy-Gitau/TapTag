package com.taptag.project.ui.composables.nfc

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneIphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.NFCScreen.NFCState
import com.taptag.project.ui.theme.NFCScannerTheme

@Composable
fun NFCScreenContent(
    nfcScreenModel: NFCScreenModel,
    state: NFCState,
    isScanning: Boolean,
    onClickStartScanning: () -> Unit,
    pulseAnimation: State<Float>,
    iconOpacity: State<Float>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "Scan & Connect",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = "Scan an NFC tag " +
//                                    "or QR code " +
                            "to connect",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(330.dp)
            ) {
                if (isScanning) {
                    Box(
                        modifier = Modifier
                            .size(200.dp * pulseAnimation.value)
                            .alpha(2f - pulseAnimation.value)
                            .clip(CircleShape)
                            .border(
                                width = 4.dp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )

                }

                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape)
                        .border(
                            width = 4.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.PhoneIphone,
                        contentDescription = null,
                        modifier = Modifier
                            .size(200.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                shape = CircleShape
                            )
                            .padding(16.dp)
                            .alpha(if (isScanning) iconOpacity.value else 1f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Text(
                    text = "Scan New Contact",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Tap the Phone with the NFC tag to scan",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (state.isScanning) {
                            nfcScreenModel.isScanning(false)
                        } else {
                            onClickStartScanning()
                        }
                    }, colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = NFCScannerTheme.PrimaryGreenLighter,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(if (state.isScanning) "Stop Scanning" else "Start Scanning")
                }

            }
        }

    }
}
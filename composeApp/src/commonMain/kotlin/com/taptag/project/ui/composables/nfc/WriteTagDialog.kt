package com.taptag.project.ui.composables.nfc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WriteTagDialog(
    isWriteMode: Boolean,
    writeStatus: String,
    onWrite: (String) -> Unit,
    onCancel: () -> Unit
) {
    var textToWrite by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            if (!isWriteMode) onCancel()
        },
        title = { Text("Write to NFC Tag") },
        text = {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (writeStatus.isNotEmpty()) {
                    Text(writeStatus)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (isWriteMode) {
                    // Show waiting UI when in write mode
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    Text("Waiting for tag...")
                } else {
                    // Show input field when not in write mode
                    OutlinedTextField(
                        value = textToWrite,
                        onValueChange = { textToWrite = it },
                        label = { Text("Enter data to write") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (isWriteMode) {
                    // Show cancel button when in write mode
                    Button(onClick = onCancel) {
                        Text("Cancel")
                    }
                } else {
                    // Show write and dismiss buttons when not in write mode
                    TextButton(onClick = onCancel) {
                        Text("Dismiss")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onWrite(textToWrite)
                        },
                        enabled = textToWrite.isNotEmpty()
                    ) {
                        Text("Write")
                    }
                }
            }
        },
        dismissButton = null
    )
}
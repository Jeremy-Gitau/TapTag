package com.taptag.project.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun ErrorDialog(
    message: String?,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)?
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Error",
                color = MaterialTheme.colorScheme.error
            )
        },
        text = {
            Text(
                text = message ?: "Unknown error occurred"
            )
        },
        confirmButton = {
            if (onRetry != null) {
                Button(onClick = { onRetry }) {
                    Text("Retry")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Dismiss")
            }
        }
    )
}

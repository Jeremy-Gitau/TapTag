package com.taptag.project.ui.composables.nfc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taptag.project.ui.theme.NFCScannerTheme

@Composable
fun SuccessContent(
    onViewContactClick: () -> Unit,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Button(
                    onClick = { onViewContactClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NFCScannerTheme.PrimaryGreen
                    ),
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(
                        text = "View Contact",
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }

        },
        title = {
            Text(
                text = "Connection Made!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        },
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = NFCScannerTheme.PrimaryGreen,
                modifier = Modifier.size(96.dp)
            )
        },
        text = {
            Box (
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Contact details saved successfully",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    )

}


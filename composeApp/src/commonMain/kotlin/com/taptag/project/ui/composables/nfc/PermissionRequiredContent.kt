package com.taptag.project.ui.composables.nfc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
fun PermissionRequiredContent(
    onRequestPermission: () -> Unit
) {
    Column(
//        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Permission icon
        Icon(
            imageVector = Icons.Default.Sensors,
            contentDescription = "NFC Permission",
            tint = NFCScannerTheme.TextWhite,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Permission message
        Text(
            text = "NFC Access Required",
            color = NFCScannerTheme.TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "To scan NFC tags, we need permission to use your device's NFC capabilities",
            color = NFCScannerTheme.TextGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Allow Access button
        Button(
            onClick = onRequestPermission,
            colors = ButtonDefaults.buttonColors(
                containerColor = NFCScannerTheme.PrimaryGreen
            ),
            modifier = Modifier.width(200.dp)
        ) {
            Text(
                text = "Allow Access",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}
package com.taptag.project.ui.composables.nfc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
fun SuccessContent(
    nfcData: String,
    onViewContactClick: (String) -> Unit
) {

    Column(
//        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success icon
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = NFCScannerTheme.PrimaryGreen,
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Success message
        Text(
            text = "Connection Made!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Contact details saved successfully",
            color = NFCScannerTheme.TextGray,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // View Contact button
        Button(
            onClick = { onViewContactClick(nfcData) },
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
}


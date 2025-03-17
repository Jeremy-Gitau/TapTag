package com.taptag.project.ui.composables.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.taptag.project.ui.theme.NFCScannerTheme

@Composable
fun AnalyticsSection(
    networkCount: Int,
    newContactsCount: Int,
    followUpsCount: Int,
    meetingsCount: Int,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSecondary,
        )
    ) {

        Column {

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AnalyticsCard(
                    icon = Icons.Outlined.People,
                    title = "Network",
                    value = networkCount.toString(),
                    subtitle = "+$newContactsCount this month",
                    subtitleColor = NFCScannerTheme.PrimaryGreen,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                AnalyticsCard(
                    icon = Icons.Outlined.WatchLater,
                    title = "Follow-ups",
                    value = followUpsCount.toString(),
                    subtitle = "$followUpsCount pending",
                    subtitleColor = Color.Red,
                    modifier = Modifier.weight(1f)
                )

            }

            HorizontalDivider()

            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AnalyticsCard(
                    icon = Icons.Outlined.CalendarToday,
                    title = "Meetings",
                    value = meetingsCount.toString(),
                    subtitle = "This week",
                    subtitleColor = Color.Blue,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider()

                AnalyticsCard(
                    icon = Icons.Outlined.Done,
                    title = "Followed Up",
                    value = meetingsCount.toString(),
                    subtitle = "$meetingsCount Followed Up",
                    subtitleColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AnalyticsCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    subtitleColor: Color,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = Modifier.padding(12.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = " title icon",
                modifier = Modifier
                    .size(25.dp)
                    .padding(end = 4.dp),
                tint = NFCScannerTheme.TextGrayDarker
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = NFCScannerTheme.TextGrayDarker
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = subtitleColor
        )
    }

}
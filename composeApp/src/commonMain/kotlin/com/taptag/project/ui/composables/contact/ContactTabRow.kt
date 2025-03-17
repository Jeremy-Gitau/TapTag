package com.taptag.project.ui.composables.contact

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.ui.theme.NFCScannerTheme

@Composable
fun ContactTabRow(
    activeTab: ContactStatus?,
    onTabSelected: (ContactStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        "All" to null,
        "Need Follow-up" to ContactStatus.PENDING,
        "Followed Up" to ContactStatus.FOLLOWED_UP,
        "Meeting Set" to ContactStatus.SCHEDULED
    )

    val selectedTabIndex = tabs.indexOfFirst { it.second == activeTab }.let {
        if (it == -1) 0 else it
    }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        tabs.forEachIndexed { index, (title, status) ->
            Tab(
                selected = index == selectedTabIndex,
                onClick = { onTabSelected(status) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                    )
                },
            )
        }
    }
}
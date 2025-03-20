package com.taptag.project.ui.composables.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.ui.composables.contact.ContactItem
import com.taptag.project.ui.composables.contact.EmptyContactsView
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.contact.ContactScreen
import com.taptag.project.ui.screens.contact.ContactState
import com.taptag.project.ui.screens.settings.UserSettingsScreen
import com.taptag.project.ui.theme.NFCScannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenContent(
    contactState: ContactState,
    onContactExpandToggled: (Int) -> Unit,
    modifier: Modifier = Modifier,
    navigator: Navigator,
    nfcScreenModel: NFCScreenModel
) {

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    ProfileHeader(
                        name = "Sarah",
                        role = "Product Manager at TechCorp"
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            navigator.parent?.push(UserSettingsScreen())
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .size(60.dp)
                        ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "profile image",
                            modifier = Modifier
                                .size(50.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = CircleShape
                                )
                                .padding(4.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                    )
            )
        }
    ) { padding ->

        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            HorizontalDivider()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {

                    Spacer(modifier = Modifier.height(16.dp))

                    // Analytics Summary
                    AnalyticsSection(
                        networkCount = contactState.networkCount,
                        newContactsCount = contactState.newContactsThisMonth,
                        followUpsCount = contactState.pendingFollowUps,
                        meetingsCount = contactState.meetingsThisWeek
                    )
                    Spacer(modifier = Modifier.height(16.dp))



                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "My Network",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.W600
                        )

                        TextButton(
                            onClick = { navigator.parent?.push(ContactScreen()) }
                        ) {
                            Text(
                                text = "Details",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.W600,
                                color = NFCScannerTheme.PrimaryGreen
                            )
                        }

                    }

                    Spacer(modifier = Modifier.height(8.dp))

//                        ContactTabRow(
//                            activeTab = contactState.activeTab,
//                            onTabSelected = onTabSelected
//                        )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (contactState.contacts.isEmpty()) {
                    item {
                        EmptyContactsView(
                            navigator = navigator,
                            startScanning = { nfcScreenModel.isScanning(true) }
                        )
                    }
                } else {
                    items(contactState.contacts) { contact ->
                        ContactItem(
                            contact = contact,
                            isExpanded = contact.id == contactState.expandedContactId,
                            onExpandToggle = { onContactExpandToggled(contact.id) }
                        )
                    }
                }

                // Add some padding at the bottom
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
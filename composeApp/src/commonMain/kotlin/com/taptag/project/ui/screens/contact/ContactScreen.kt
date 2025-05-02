package com.taptag.project.ui.screens.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.ui.composables.contact.ContactItem
import com.taptag.project.ui.composables.contact.ContactScreenSearchBar
import com.taptag.project.ui.composables.contact.ContactTabRow
import com.taptag.project.ui.composables.contact.EmptyContactsView
import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel

class ContactScreen() : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val contactScreenModel: ContactScreenModel = koinScreenModel()
        val nfcScreenModel: NFCScreenModel = koinScreenModel()

        val contactState by contactScreenModel.state.collectAsState()
        val nfcState by nfcScreenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(contactState.contacts) {

            contactScreenModel.fetchAllContacts()

        }

        Scaffold(

            topBar = {

                TopAppBar(
                    title = {
                        if (!contactState.searchState) {
                            Text(
                                text = "Contacts",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        } else {

                            ContactScreenSearchBar(
                                contactScreenModel = contactScreenModel,
                                contactState = contactState
                            )

                        }

                    },
                    navigationIcon = {
                        if (!contactState.searchState) {
                            IconButton(onClick = { navigator.pop() }) {

                                Icon(
                                    imageVector = Icons.Default.ArrowBackIosNew,
                                    contentDescription = "contact back arrow"
                                )

                            }
                        }
                    },
                    actions = {
                        if (!contactState.searchState) {
                            IconButton(onClick = {
                                contactScreenModel.toggleSearchBar(true)
                            }) {

                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "search contacts"
                                )

                            }
                        }
                    },
                    modifier = Modifier
                        .height(if (contactState.searchState) 130.dp else 80.dp),
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                item {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        ContactTabRow(
                            activeTab = contactState.activeTab,
                            onTabSelected = contactScreenModel::onTabSelected
                        )
                    }
                }

                if (contactState.contacts.isEmpty()) {
                    item {
                        EmptyContactsView(
                            navigator = navigator
                        )
                    }
                } else {
                    items(contactState.contacts) { contact ->
                        ContactItem(
                            contact = contact,
                            isExpanded = contact.id == contactState.expandedContactId,
                            onExpandToggle = { contactScreenModel.toggleContactExpand(contact.id) }
                        )
                    }
                }
            }
        }

    }
}
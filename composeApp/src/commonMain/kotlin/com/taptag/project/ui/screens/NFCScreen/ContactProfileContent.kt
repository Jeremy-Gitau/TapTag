package com.taptag.project.ui.screens.NFCScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Whatsapp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.data.platformShareHandler.getPlatformShareHandler
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.ui.common.ActionButton
import com.taptag.project.ui.common.ProfileImage
import com.taptag.project.ui.common.ProfileInfoItem
import com.taptag.project.ui.common.SuccessConfirmationOverlay
import com.taptag.project.ui.screens.contact.ContactScreenModel
import kotlinx.coroutines.delay

class ContactProfileContent : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val scrollState = rememberScrollState()

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val state by nfcScreenModel.state.collectAsState()

        val contactScreenModel: ContactScreenModel = koinScreenModel()
        val contactState by contactScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        val shareHandler = getPlatformShareHandler()

        // Handle saved confirmation animation
        LaunchedEffect(contactState.showSavedConfirmation) {
            if (contactState.showSavedConfirmation) {
                delay(2000)
                contactScreenModel.toggleShowSavedConfirmation(false)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Contact Profile",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        if (contactState.isEditMode) {
                            IconButton(
                                onClick = { contactScreenModel.toggleIsEditMode(false) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "cancel save contact",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {

                    if (contactState.isEditMode) {
                        FloatingActionButton(
                            onClick = {
                                // Save contact logic
                                contactScreenModel.toggleShowSavedConfirmation(true)
                                contactScreenModel.toggleIsEditMode(false)
                                contactScreenModel.saveContact(ContactsRequestDomain(
                                    name = state.currentContact.name,
                                    email = state.currentContact.email,
                                    phone = "",
                                    notes = state.currentContact.notes,
                                    company = state.currentContact.company,
                                    tags = state.currentContact.tags
                                ))
                            },
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save Contact"
                            )
                        }
                    } else {

                        FloatingActionButton(
                            onClick = {
                                nfcScreenModel.observeCurrentContact(state.currentContact)
                                contactScreenModel.toggleIsEditMode(true)
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Contact"
                            )
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Main content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    state.currentContact.let { contact ->
                        // Profile header
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                                .padding(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Profile image
                                ProfileImage(
                                    text = contact.name.take(2).uppercase(),
                                    editImage = false
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Name
                                Text(
                                    text = contact.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                // Role and Company
                                if (contact.role.isNotEmpty() || contact.company.isNotEmpty()) {
                                    Text(
                                        text = listOfNotNull(contact.role, contact.company)
                                            .joinToString(" at "),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Quick action buttons
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    ActionButton(
                                        icon = Icons.Default.Email,
                                        label = "Email",
                                        onClick = {
                                            shareHandler.sendEmail(contact.email)
                                        }
                                    )

                                    ActionButton(
                                        icon = Icons.Outlined.Sms,
                                        label = "Text",
                                        onClick = {
//                                            shareHandler.sendSms(contact.phoneNumber)
                                        }
                                    )

                                    ActionButton(
                                        icon = Icons.Default.Whatsapp,
                                        label = "WhatsApp",
                                        onClick = {
//                                            shareHandler.openWhatsApp(contact.phoneNumber)
                                        }
                                    )
                                }
                            }
                        }

                        // Contact details card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Contact Details",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                ProfileInfoItem(
                                    icon = Icons.Default.AccountCircle,
                                    label = "Name",
                                    value = contact.name,
                                    isEdit = contactState.isEditMode,
                                    textToWrite = contact.name,
                                    onValueChanged = {
                                        nfcScreenModel.observeCurrentContact(
                                            state.currentContact.copy(
                                                name = it
                                            )
                                        )
                                    }
                                )

                                if (contact.email.isNotEmpty()) {
                                    ProfileInfoItem(
                                        icon = Icons.Default.Email,
                                        label = "Email",
                                        value = contact.email,
                                        isEdit = contactState.isEditMode,
                                        textToWrite = contact.email,
                                        onValueChanged = {
                                            nfcScreenModel.observeCurrentContact(
                                                state.currentContact.copy(
                                                    email = it
                                                )
                                            )
                                        }
                                    )
                                }

                                if (contact.role.isNotEmpty()) {
                                    ProfileInfoItem(
                                        icon = Icons.Default.Work,
                                        label = "Role",
                                        value = contact.role,
                                        isEdit = contactState.isEditMode,
                                        textToWrite = contact.role,
                                        onValueChanged = {
                                            nfcScreenModel.observeCurrentContact(
                                                state.currentContact.copy(
                                                    role = it
                                                )
                                            )
                                        }
                                    )
                                }

                                if (contact.company.isNotEmpty()) {
                                    ProfileInfoItem(
                                        icon = Icons.Default.Work,
                                        label = "Company",
                                        value = contact.company,
                                        isEdit = contactState.isEditMode,
                                        textToWrite = contact.company,
                                        onValueChanged = {
                                            nfcScreenModel.observeCurrentContact(
                                                state.currentContact.copy(
                                                    company = it
                                                )
                                            )
                                        }
                                    )
                                }

//                                if (contact.status.getLabel().isNotEmpty()) {
//                                    ProfileInfoItem(
//                                        icon = Icons.Default.Stars,
//                                        label = "Status",
//                                        value = contact.status.getLabel(),
//                                        isEdit = contactState.isEditMode,
//                                        textToWrite = contact.status.getLabel(),
//                                        onValueChanged = {
//                                            nfcScreenModel.observeCurrentContact(
//                                                state.currentContact.copy(
//                                                    status = it
//                                                )
//                                            )
//                                        }
//                                    )
//                                }

                            }
                        }

                        // Tags section
                        if (contact.tags.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Tag,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Tags",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }

                                    // Here you would display the tags
                                    Text(
                                        text = contact.tags.joinToString(", "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        // Notes section
                        if (contact.notes.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Note,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Notes",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }

                                    Text(
                                        text = contact.notes,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(100.dp)) // Space for FABs
                    }
                }

                // Success confirmation overlay
                SuccessConfirmationOverlay(
                    state = contactState.showSavedConfirmation,
                    text = "Contact Saved"
                )
            }
        }
    }
}



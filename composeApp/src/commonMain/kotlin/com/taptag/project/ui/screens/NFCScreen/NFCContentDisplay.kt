package com.taptag.project.ui.screens.NFCScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.taptag.project.data.nfcManager.getNFCManager
import com.taptag.project.data.platformShareHandler.getPlatformShareHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NFCContentDisplay : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val snackBarHostState = remember { SnackbarHostState() }
        var showSavedConfirmation by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()

        val nfcScreenModel: NFCScreenModel = koinScreenModel()
        val state by nfcScreenModel.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow
        val nfcManager = getNFCManager()

        val shareHandler = getPlatformShareHandler()

        nfcManager.RegisterApp()

        scope.launch {
            nfcManager.tags.collectLatest { tagData ->
                println("I have detected a tag $tagData")
                nfcScreenModel.readTag(tagData)
            }
        }

        // Listen for write results
        scope.launch {
            nfcManager.writeResult.collectLatest { success ->
                nfcScreenModel.toggleWriteStatus(
                    if (success) {
                        "Tag written successfully!"
                    } else {
                        "Failed to write to tag. Please try again."
                    }
                )
                nfcScreenModel.toggleIsWriteMode(!success) // Stay in write mode if failed
                if (success) {
                    // Reset UI after successful write
                    nfcScreenModel.toggleShowWriteDialog(false)
                    nfcScreenModel.toggleResultDialog(true)
                    nfcScreenModel.isSuccess(success, "Tag written successfully with custom data")
                }
            }
        }

        // Handle saved confirmation animation
        LaunchedEffect(showSavedConfirmation) {
            if (showSavedConfirmation) {
                delay(2000)
                showSavedConfirmation = false
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
                    )
                )
            },
            floatingActionButton = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            // Save contact logic
                            scope.launch {
                                snackBarHostState.showSnackbar("Contact saved to your contacts")
                            }
                            showSavedConfirmation = true
                        },
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save Contact"
                        )
                    }

                    FloatingActionButton(
                        onClick = { nfcScreenModel.observeCurrentContact(state.currentContact) },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Contact"
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) { data ->
                    Snackbar(
                        modifier = Modifier.padding(16.dp),
                        action = {
                            Button(onClick = { data.dismiss() }) {
                                Text("OK")
                            }
                        }
                    ) {
                        Text(data.visuals.message)
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
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contact.name.take(2).uppercase(),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 48.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

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
                                        icon = Icons.Default.Share,
                                        label = "Share",
                                        onClick = { /* Share action */ }
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

                                ContactProfileItem(
                                    icon = Icons.Default.AccountCircle,
                                    label = "Name",
                                    value = contact.name
                                )

                                if (contact.email.isNotEmpty()) {
                                    ContactProfileItem(
                                        icon = Icons.Default.Email,
                                        label = "Email",
                                        value = contact.email
                                    )
                                }

                                if (contact.role.isNotEmpty()) {
                                    ContactProfileItem(
                                        icon = Icons.Default.Work,
                                        label = "Role",
                                        value = contact.role
                                    )
                                }

                                if (contact.company.isNotEmpty()) {
                                    ContactProfileItem(
                                        icon = Icons.Default.Work,
                                        label = "Company",
                                        value = contact.company
                                    )
                                }

                                if (contact.lastContact.isNotEmpty()) {
                                    ContactProfileItem(
                                        icon = Icons.Default.Stars,
                                        label = "Last Contact",
                                        value = contact.lastContact
                                    )
                                }

                                if (contact.status.getLabel().isNotEmpty()) {
                                    ContactProfileItem(
                                        icon = Icons.Default.Stars,
                                        label = "Status",
                                        value = contact.status.getLabel()
                                    )
                                }

                                if (contact.relationshipStrength.name.isNotEmpty()) {
                                    ContactProfileItem(
                                        icon = Icons.Default.Stars,
                                        label = "Relationship",
                                        value = contact.relationshipStrength.name
                                    )
                                }
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
                AnimatedVisibility(
                    visible = showSavedConfirmation,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f))
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.size(200.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Contact Saved",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactProfileItem(icon: ImageVector, label: String, value: String) {
    if (value.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Text(
                text = value,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 28.dp, top = 4.dp)
            )

            Divider(modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
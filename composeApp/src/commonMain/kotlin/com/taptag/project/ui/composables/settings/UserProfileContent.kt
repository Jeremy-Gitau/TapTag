package com.taptag.project.ui.composables.settings

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
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.Close
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
import com.taptag.project.ui.common.ProfileImage
import com.taptag.project.ui.common.ProfileInfoItem
import com.taptag.project.ui.common.SuccessConfirmationOverlay
import com.taptag.project.ui.screens.authentication.UserScreenModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
class UserProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scrollState = rememberScrollState()

        val userScreenModel: UserScreenModel = koinScreenModel()
        val userState by userScreenModel.state.collectAsState()

        // Handle saved confirmation animation
        LaunchedEffect(userState.showSavedConfirmation) {
            if (userState.showSavedConfirmation) {
                delay(2000)
                userScreenModel.toggleShowSavedConfirmation(false)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "User Profile",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    actions = {
                        if (userState.isEditMode) {
                            IconButton(
                                onClick = { userScreenModel.toggleIsEditMode(false) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "cancel save profile",
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
                    if (userState.isEditMode) {
                        FloatingActionButton(
                            onClick = {

                                userScreenModel.toggleShowSavedConfirmation(true)

                                userScreenModel.toggleIsEditMode(false)

                                userState.userProfile.let {
                                    userScreenModel.updateUserProfile(
                                        data = it,
                                        id = userState.userProfile.id.toString()
                                    )
                                }
                            },
                            containerColor = MaterialTheme.colorScheme.secondary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Save Profile"
                            )
                        }
                    } else {

                        FloatingActionButton(
                            onClick = {
                                userScreenModel.toggleIsEditMode(true)
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile"
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
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
                            // Profile image with camera icon
                            ProfileImage(
                                text = userState.currentUser?.user?.firstName?.take(2)
                                    ?.uppercase()
                                    .toString(),
                                editImage = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Name
                            Text(
                                text = userState.currentUser?.user?.firstName.toString(),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Email
                            Text(
                                text = userState.currentUser?.user?.email.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                        }
                    }

                    // Profile details card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Personal Information",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            ProfileInfoItem(
                                icon = Icons.Default.Person,
                                label = "Full Name",
                                value = userState.currentUser?.user?.firstName.toString(),
                                isEdit = userState.isEditMode,
                                textToWrite = userState.userProfile.firstName.toString(),
                                onValueChanged = {
                                    userScreenModel.handleProfileUpdate(
                                        userState.userProfile.copy(
                                            firstName = it
                                        )
                                    )
                                }
                            )

                            ProfileInfoItem(
                                icon = Icons.Default.Email,
                                label = "Email Address",
                                value = userState.currentUser?.user?.email.toString(),
                                isEdit = userState.isEditMode,
                                textToWrite = userState.userProfile.email.toString(),
                                onValueChanged = {
                                    userScreenModel.handleProfileUpdate(
                                        userState.userProfile.copy(
                                            email = it
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp)) // Space for FABs
                }

                // Success confirmation overlay
                SuccessConfirmationOverlay(
                    state = userState.showSavedConfirmation,
                    text = "Profile Saved"
                )
            }
        }
    }

}
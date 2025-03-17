package com.taptag.project.ui.composables.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Upgrade
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.ui.screens.authentication.AuthState
import com.taptag.project.ui.screens.settings.SettingsScreenModel
import com.taptag.project.ui.screens.settings.SettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSettingsScreenContent(
    navigator: Navigator, settingsState: SettingsState, settingsScreenModel: SettingsScreenModel,
    onClickLogOut: () -> Unit,
    onDismiss: (Boolean) -> Unit,
    authState: AuthState
) {

    Scaffold(

        topBar = {

            TopAppBar(title = {
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }, navigationIcon = {
                IconButton(onClick = {
                    navigator.pop()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBackIosNew,
                        contentDescription = "settings nav icon"
                    )
                }
            }, actions = {

                IconButton(onClick = {
                    settingsScreenModel.observeDarkMode(!settingsState.isDarkMode)
                }) {
                    Icon(
                        imageVector = if (!settingsState.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "theme icon"
                    )
                }
            }, colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
            )
            )
        }) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {

            if (authState.toggleLogOutDialog){
                LogOutDialog(
                    onDismiss = { onDismiss(false) },
                    onLogOutClicked = { onClickLogOut() }
                )
            }

            LazyColumn {

                item {

                    HorizontalDivider()

                    Column(
                        modifier = Modifier.padding(
                            16.dp
                        )
                    ) {

                        Text(
                            text = "Account",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.background,
                                disabledContentColor = MaterialTheme.colorScheme.background,
                            )
                        ) {
                            SettingsCard(text = "Profile Info",
                                icon = Icons.Outlined.Person,
                                onNavigatorClicked = {})

                            SettingsCard(text = "Change Password",
                                icon = Icons.Default.Password,
                                onNavigatorClicked = {})

                            SettingsCard(text = "Upgrade",
                                icon = Icons.Default.Upgrade,
                                onNavigatorClicked = {})
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            colors = CardColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.background,
                                disabledContentColor = MaterialTheme.colorScheme.background,
                            )
                        ) {
                            SettingsCard(
                                text = "Log Out",
                                icon = Icons.AutoMirrored.Filled.Logout,
                                onNavigatorClicked = {

                                    onDismiss(true)
                                })
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsCard(
    text: String,
    icon: ImageVector,
    onNavigatorClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onNavigatorClicked() }),
        contentAlignment = Alignment.Center
    ) {
        Column(

            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 16.dp)
                    )

                    Text(text = text)

                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 6.dp)
                )
            }


            HorizontalDivider()
        }


    }
}


@Composable
fun LogOutDialog(
    onDismiss: () -> Unit,
    onLogOutClicked: () -> Unit
){

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "cancel log out",
                        tint = MaterialTheme.colorScheme.error
                    )
                }

                Button(onClick = { onLogOutClicked() }) {
                    Text(text = "Log Out")
                }
            }

        },
        title = {
            Text(
                text = "Are you sure you want to Log Out?",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {

        }
    )
}
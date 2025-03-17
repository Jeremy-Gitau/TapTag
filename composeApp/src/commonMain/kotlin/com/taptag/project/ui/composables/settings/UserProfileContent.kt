package com.taptag.project.ui.composables.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.ui.theme.NFCScannerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileContent(
    navigator: Navigator
) {

    Scaffold(

        topBar = {

            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.pop()
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBackIosNew,
                            contentDescription = "profile nav icon"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {

            LazyColumn {

                item {

                    Column(

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "profile pic",
                                modifier = Modifier
                                    .size(130.dp)
                                    .background(
                                        color = NFCScannerTheme.PrimaryGreenLighter.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .padding(8.dp)
                            )

                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "edit profile",
                                modifier = Modifier
                                    .background(
                                        color = NFCScannerTheme.PrimaryGreenLighter.copy(alpha = 0.3f),
                                        shape = CircleShape
                                    )
                                    .padding(8.dp)
                            )

                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "user name",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text = "Email@gmail.com",
                            style = MaterialTheme.typography.labelMedium,
                            color = NFCScannerTheme.TextGray
                        )

                        HorizontalDivider()
                    }
                }

            }


        }
    }
}
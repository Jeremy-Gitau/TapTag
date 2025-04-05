package com.taptag.project.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.taptag.project.ui.screens.NFCScreen.NFCScreen
import com.taptag.project.ui.screens.analytics.AnalyticsScreen

class HomeScreen() : Screen {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {

        val navigationItem = listOf(

            NavigationItem(
//                title = "Analytics",
                icon = Icons.Default.Dashboard,
                screen = AnalyticsScreen()
            ),

            NavigationItem(
//                title = "Scan",
                icon = Icons.Default.PhoneIphone,
                screen = NFCScreen()
            )
        )

        var selectedTab by remember { mutableIntStateOf(0) }

        Navigator(screen = AnalyticsScreen()) { navigator ->
            Scaffold(
                bottomBar = {
                    NavigationBar(
//                        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                        containerColor = MaterialTheme.colorScheme.background,
                    ) {

//                        HorizontalDivider()

                        navigationItem.forEachIndexed { index, item ->

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                onClick = {
                                    selectedTab = index
                                    navigator.push(navigationItem[selectedTab].screen)
                                },
                                selected = selectedTab == index,
                                label = { item.title?.let { Text(it) } },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )

                        }
                    }
                }
            ) {
                navigator.lastItem.Content()
            }
        }


    }
}

data class NavigationItem(
    val title: String? = null,
    val icon: ImageVector,
    val screen: Screen
)
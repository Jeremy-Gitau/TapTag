package com.taptag.project

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.registry.screenModule
import com.taptag.project.ui.screens.settings.SettingsScreenModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        setContent {

            val settingsScreenModel: SettingsScreenModel = koinInject()
            val settingsState by settingsScreenModel.state.collectAsState()

            val isDarkMode = settingsState.isDarkMode.collectAsState(initial = isSystemInDarkTheme()).value

            LaunchedEffect(Unit) {

                settingsScreenModel.observeDarkMode()

            }

            println("dark mode state is: $isDarkMode")

            App(isDarkMode = isDarkMode)

        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.taptag.project.ui.screens.settings.SettingsScreenModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
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

            val settingsScreenModel: SettingsScreenModel = koinInject<SettingsScreenModel>()
            val settingsState = settingsScreenModel.state.collectAsState().value

            val isDarkMode by settingsState.isDarkMode.collectAsState(initial = isSystemInDarkTheme())

            println("dark mode state is: $isDarkMode")

            LaunchedEffect(Unit) {
                settingsScreenModel.observeDarkMode()
                println("launched dark mode state is: $isDarkMode")
            }

            App(isDarkMode = isDarkMode)

        }
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
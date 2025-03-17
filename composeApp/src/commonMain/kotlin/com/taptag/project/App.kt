package com.taptag.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.taptag.project.ui.appNavigation.AppNavigation
import com.taptag.project.ui.screens.settings.SettingsScreenModel
import com.taptag.project.ui.theme.NFCScannerTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    val settingsScreenModel = SettingsScreenModel()
    val state by settingsScreenModel.state.collectAsState()


    NFCScannerTheme(
        darkTheme = state.isDarkMode
    ) {
        AppNavigation()
    }

}
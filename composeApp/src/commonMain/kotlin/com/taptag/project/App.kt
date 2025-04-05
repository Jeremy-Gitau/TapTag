package com.taptag.project

import androidx.compose.runtime.Composable
import com.taptag.project.ui.appNavigation.AppNavigation
import com.taptag.project.ui.theme.NFCScannerTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(isDarkMode: Boolean? = null) {

    println("dark mode in APP is: $isDarkMode")

    NFCScannerTheme(
        darkTheme = isDarkMode!!
    ) {
        AppNavigation()
    }

}
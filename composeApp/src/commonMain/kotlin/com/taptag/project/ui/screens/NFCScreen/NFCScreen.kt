package com.taptag.project.ui.screens.NFCScreen

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.taptag.project.ui.composables.nfc.NFCHomeScreenContent

class NFCScreen : Screen {

    @Composable
    override fun Content() {

        NFCHomeScreenContent()

    }
}
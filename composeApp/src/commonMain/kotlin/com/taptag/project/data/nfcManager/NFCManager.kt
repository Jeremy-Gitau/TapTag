package com.taptag.project.data.nfcManager

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow

expect open class NFCManager {

    val tags: SharedFlow<String>

    @Composable
    fun RegisterApp()

}

@Composable
expect fun getNFCManager(): NFCManager
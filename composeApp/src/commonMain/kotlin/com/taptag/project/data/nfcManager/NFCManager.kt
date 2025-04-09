package com.taptag.project.data.nfcManager

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow

expect open class NFCManager {

    val tags: SharedFlow<String>

    val writeResult: SharedFlow<Boolean>

    @Composable
    fun RegisterApp()

    fun prepareWrite(data: String)

    fun cancelWrite()

}

@Composable
expect fun getNFCManager(): NFCManager
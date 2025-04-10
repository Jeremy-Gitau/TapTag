package com.taptag.project.data.nfcManager

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow

actual open class NFCManager {

    actual val tags: SharedFlow<String> = TODO()

    actual val writeResult: SharedFlow<Boolean> = TODO()

    @Composable
    actual fun RegisterApp() {
    }

    actual fun prepareWrite(data: String) {}

    actual fun cancelWrite() {}

}

@Composable
actual fun getNFCManager(): NFCManager {
    TODO("Not yet implemented")
}
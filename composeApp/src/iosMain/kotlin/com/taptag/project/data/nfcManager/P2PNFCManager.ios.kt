@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.nfcManager

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow

actual class PlatformIntent

actual open class P2PNFCManager {

    actual val receivedMessages: SharedFlow<String>
        get() {
            TODO()
        }
    actual val sendStatus: SharedFlow<Boolean> = TODO()
    actual val connectionState: SharedFlow<P2PConnectionState>

    @Composable
    actual fun RegisterApp() {
    }

    actual fun setOutgoingMessage(message: String) {}
    actual fun processIntent(intent: PlatformIntent) {}
    actual fun isNfcAvailable(): Boolean {
        return TODO()
    }

}

@Composable
actual fun getP2PNFCManager(): P2PNFCManager {
    TODO("Not yet implemented")
}

actual enum class P2PConnectionState {
    NOT_AVAILABLE,
    DISABLED,
    READY,
    SENDING,
    RECEIVING,
    ERROR
}
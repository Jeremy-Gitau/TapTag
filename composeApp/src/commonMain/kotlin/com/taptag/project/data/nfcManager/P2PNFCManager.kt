@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.nfcManager

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.SharedFlow

expect class PlatformIntent

expect open class P2PNFCManager {

    val receivedMessages: SharedFlow<String>

    val sendStatus: SharedFlow<Boolean>

    val connectionState: SharedFlow<P2PConnectionState>

    @Composable
    fun RegisterApp()

    fun setOutgoingMessage(message: String)

    fun processIntent(intent: PlatformIntent)

    fun isNfcAvailable(): Boolean

}

@Composable
expect fun getP2PNFCManager(): P2PNFCManager

expect enum class P2PConnectionState
@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.nfcManager

import android.app.Activity
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcAdapter.CreateNdefMessageCallback
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.nio.charset.Charset

actual typealias PlatformIntent = Intent

private const val TAG = "P2PNFCManager"

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual open class P2PNFCManager {

    private var nfcAdapter: NfcAdapter? = null
    private val scope = CoroutineScope(SupervisorJob())

    // Flow for received messages
    private val _receivedMessages = MutableSharedFlow<String>()
    actual val receivedMessages: SharedFlow<String> = _receivedMessages

    // Flow for send operation status
    private val _sendStatus = MutableSharedFlow<Boolean>()
    actual val sendStatus: SharedFlow<Boolean> = _sendStatus

    // Flow for connection state
    private val _connectionState = MutableSharedFlow<P2PConnectionState>()
    actual val connectionState: SharedFlow<P2PConnectionState> = _connectionState

    // Message to send to peer
    private var outgoingMessage: String = ""

    // NFC callbacks
    private val ndefMessageCallback = CreateNdefMessageCallback { event ->
        Log.d(TAG, "Creating NDEF message to send")

        scope.launch {
            _connectionState.emit(P2PConnectionState.SENDING)
        }

        val textRecord = NdefRecord.createTextRecord("en", outgoingMessage)
        val appRecord = NdefRecord.createApplicationRecord("com.taptag.project")

        NdefMessage(arrayOf(textRecord, appRecord))
    }

    private val ndefPushCompleteCallback = NfcAdapter.OnNdefPushCompleteCallback { event ->
        Log.d(TAG, "NDEF message sent successfully")

        scope.launch {
            _sendStatus.emit(true)
            _connectionState.emit(P2PConnectionState.READY)
        }
    }

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        Log.d(TAG, "Tag discovered in reader mode: ${tag?.id?.joinToString(", ") ?: "null"}")
    }

    /**
     * Register the app with NFC subsystem and initialize P2P communication
     */
    @Composable
    actual fun RegisterApp() {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val activity = context as? Activity

        // Initialize NFC adapter
        nfcAdapter = remember { NfcAdapter.getDefaultAdapter(context) }

        DisposableEffect(nfcAdapter) {
            if (activity == null) {
                Log.e(TAG, "Context is not an Activity")
                onDispose { }
                return@DisposableEffect onDispose {  }
            }

            val lifecycleObserver = object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    setupNfcModes(activity)
                }

                override fun onPause(owner: LifecycleOwner) {
                    disableNfcModes(activity)
                }
            }

            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                disableNfcModes(activity)
            }
        }
    }

    /**
     * Set up NFC for both P2P and reader modes
     */
    private fun setupNfcModes(activity: Activity) {
        nfcAdapter?.let { adapter ->
            // Set up Android Beam (P2P mode)
            adapter.enableForegroundDispatch(
                activity,
                // Intent to be used when a tag is discovered
                android.app.PendingIntent.getActivity(
                    activity, 0,
                    Intent(activity, activity.javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    android.app.PendingIntent.FLAG_MUTABLE
                ),
                // IntentFilters for detecting NFC intents
                arrayOf(
                    android.content.IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
                        addDataType("*/*")
                    }
                ),
                // Array of tag technologies to handle
                arrayOf(arrayOf(android.nfc.tech.Ndef::class.java.name))
            )

            // Check if Android Beam is available (Android 10+)
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                try {
                    // Use reflection to access setNdefPushMessageCallback (deprecated in API 29)
                    val method = NfcAdapter::class.java.getMethod(
                        "setNdefPushMessageCallback",
                        CreateNdefMessageCallback::class.java,
                        Activity::class.java
                    )
                    method.invoke(adapter, ndefMessageCallback, activity)

                    // Use reflection to access setOnNdefPushCompleteCallback
                    val completeMethod = NfcAdapter::class.java.getMethod(
                        "setOnNdefPushCompleteCallback",
                        NfcAdapter.OnNdefPushCompleteCallback::class.java,
                        Activity::class.java
                    )
                    completeMethod.invoke(adapter, ndefPushCompleteCallback, activity)
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up NFC P2P mode", e)
                }
            }

            // Set up reader mode
            val options = Bundle().apply {
                putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 500)
            }

            adapter.enableReaderMode(
                activity,
                readerCallback,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                options
            )

            scope.launch {
                _connectionState.emit(P2PConnectionState.READY)
            }

            Log.d(TAG, "NFC P2P and Reader modes enabled")
        } ?: run {
            Log.e(TAG, "NFC not available on this device")
            scope.launch {
                _connectionState.emit(P2PConnectionState.NOT_AVAILABLE)
            }
        }
    }

    /**
     * Disable NFC modes
     */
    private fun disableNfcModes(activity: Activity) {
        nfcAdapter?.let { adapter ->
            try {
                adapter.disableReaderMode(activity)
                adapter.disableForegroundDispatch(activity)

                // For older Android versions
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
                    try {
                        // Use reflection to access setNdefPushMessage (deprecated in API 29)
                        val method = NfcAdapter::class.java.getMethod(
                            "setNdefPushMessage",
                            NdefMessage::class.java,
                            Activity::class.java
                        )
                        method.invoke(adapter, null, activity)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error disabling NFC P2P mode", e)
                    }
                }

                scope.launch {
                    _connectionState.emit(P2PConnectionState.DISABLED)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error disabling NFC modes", e)
            }

            Log.d(TAG, "NFC P2P and Reader modes disabled")
        }
    }

    /**
     * Set the message to be sent to a peer device
     */
    actual fun setOutgoingMessage(message: String) {
        outgoingMessage = message
        Log.d(TAG, "Outgoing message set: $message")
    }

    /**
     * Process intent from activity for NFC operations
     */
    actual fun processIntent(intent: PlatformIntent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.let { rawMessages ->
                rawMessages.forEach { rawMessage ->
                    val ndefMessage = rawMessage as? NdefMessage
                    ndefMessage?.let { processReceivedNdefMessage(it) }
                }
            }
        }
    }

    /**
     * Check if NFC is available and enabled on the device
     */
    actual fun isNfcAvailable(): Boolean {
        return nfcAdapter != null && nfcAdapter?.isEnabled == true
    }

    /**
     * Process a received NDEF message
     */
    private fun processReceivedNdefMessage(ndefMessage: NdefMessage) {
        val records = ndefMessage.records

        scope.launch {
            _connectionState.emit(P2PConnectionState.RECEIVING)
        }

        if (records.isNotEmpty()) {
            records.forEach { record ->
                // Skip Android Application Record (AAR)
                if (record.tnf == NdefRecord.TNF_EXTERNAL_TYPE) {
                    return@forEach
                }

                try {
                    // Parse payload, taking into account language code
                    val payload = record.payload
                    val textData = if (payload.isNotEmpty()) {
                        if (payload[0].toInt() and 0x80 == 0) {
                            // UTF-8 encoding
                            val languageCodeLength = payload[0].toInt() and 0x3F
                            if (payload.size > 1 + languageCodeLength) {
                                String(
                                    payload, 1 + languageCodeLength,
                                    payload.size - 1 - languageCodeLength,
                                    Charset.forName("UTF-8")
                                )
                            } else {
                                ""
                            }
                        } else {
                            // UTF-16 encoding
                            val languageCodeLength = payload[0].toInt() and 0x3F
                            if (payload.size > 1 + languageCodeLength) {
                                String(
                                    payload, 1 + languageCodeLength,
                                    payload.size - 1 - languageCodeLength,
                                    Charset.forName("UTF-16")
                                )
                            } else {
                                ""
                            }
                        }
                    } else {
                        ""
                    }

                    if (textData.isNotEmpty()) {
                        Log.d(TAG, "Received message: $textData")
                        scope.launch {
                            _receivedMessages.emit(textData)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing NDEF record", e)
                    scope.launch {
                        _connectionState.emit(P2PConnectionState.ERROR)
                    }
                }
            }
        }

        scope.launch {
            _connectionState.emit(P2PConnectionState.READY)
        }
    }
}

@Composable
actual fun getP2PNFCManager(): P2PNFCManager {
    return remember { P2PNFCManager() }
}

actual enum class P2PConnectionState {
    NOT_AVAILABLE,
    DISABLED,
    READY,
    SENDING,
    RECEIVING,
    ERROR
}
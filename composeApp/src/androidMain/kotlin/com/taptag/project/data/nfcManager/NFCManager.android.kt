package com.taptag.project.data.nfcManager

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef.get
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

private const val TAG = "NFCManager"
private const val READER_PRESENCE_CHECK_DELAY = 500
private const val ULTRALIGHT_PAGES_TO_READ = 4

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual open class NFCManager : NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private val _tagData = MutableSharedFlow<String>()
    private val scope = CoroutineScope(SupervisorJob())
    actual val tags: SharedFlow<String> = _tagData

    @Composable
    actual fun RegisterApp() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(LocalContext.current)

        nfcAdapter?.let { adapter ->
            val options = Bundle().apply {
                putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, READER_PRESENCE_CHECK_DELAY)
            }

            adapter.enableReaderMode(
                LocalContext.current as Activity,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    override fun onTagDiscovered(tag: Tag?) {
        tag ?: run {
            Log.w(TAG, "Discovered tag is null")
            return
        }

        Log.d(TAG, "Tag discovered: ${tag.id?.joinToString(", ")}")

        val ndefMessage = readNdefMessage(tag)

        if (ndefMessage != null) {
            processNdefMessage(ndefMessage)
        } else {
            handleRawTag(tag)
        }
    }

    private fun readNdefMessage(tag: Tag): NdefMessage? {
        val mNdef = get(tag) ?: return null

        return try {
            mNdef.connect()
            val message = mNdef.ndefMessage
            if (message == null) {
                Log.w(TAG, "Tag is NDEF compatible but doesn't contain a message")
            }
            message
        } catch (e: Exception) {
            Log.e(TAG, "Error reading NDEF message", e)
            null
        } finally {
            try {
                mNdef.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing NDEF connection", e)
            }
        }
    }

    private fun processNdefMessage(ndefMessage: NdefMessage) {
        val records = ndefMessage.records

        if (records.isNotEmpty()) {
            records.forEach { record ->
                val payload = String(record.payload, Charsets.UTF_8)
                scope.launch {
                    _tagData.emit(payload)
                }
            }
        } else {
            Log.d(TAG, "No NDEF records found in tag")
        }
    }

    private fun handleRawTag(tag: Tag) {
        val tagData = StringBuilder().apply {
            append("Tag ID: ${bytesToHex(tag.id)}\n")
            append("Technologies: ${tag.techList.joinToString(", ")}\n")
        }

        readMifareClassicData(tag, tagData)
        readMifareUltralightData(tag, tagData)

        scope.launch {
            _tagData.emit(tagData.toString())
        }
    }

    private fun readMifareClassicData(tag: Tag, tagData: StringBuilder) {
        if (!tag.techList.contains(MifareClassic::class.java.name)) return

        val mifareClassic = MifareClassic.get(tag)
        try {
            mifareClassic.connect()
            tagData.append("Mifare Classic - Size: ${mifareClassic.size} bytes\n")
        } catch (e: Exception) {
            Log.e(TAG, "Error reading Mifare Classic", e)
        } finally {
            try {
                mifareClassic.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing Mifare Classic", e)
            }
        }
    }

    private fun readMifareUltralightData(tag: Tag, tagData: StringBuilder) {
        if (!tag.techList.contains(MifareUltralight::class.java.name)) return

        val mifareUltralight = MifareUltralight.get(tag)
        try {
            mifareUltralight.connect()
            tagData.append("Mifare Ultralight - Type: ${mifareUltralight.type}\n")

            try {
                tagData.append("Data: ")
                for (i in 0 until ULTRALIGHT_PAGES_TO_READ) {
                    val page = mifareUltralight.readPages(i)
                    tagData.append(bytesToHex(page))
                    tagData.append(" ")
                }
                tagData.append("\n")
            } catch (e: Exception) {
                Log.e(TAG, "Error reading Mifare Ultralight pages", e)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error reading Mifare Ultralight", e)
        } finally {
            try {
                mifareUltralight.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing Mifare Ultralight", e)
            }
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789ABCDEF"
        return bytes.joinToString("") { byte ->
            val i = byte.toInt() and 0xFF
            "${hexChars[i shr 4]}${hexChars[i and 0x0F]}"
        }
    }
}

@Composable
actual fun getNFCManager(): NFCManager {
    return NFCManager()
}
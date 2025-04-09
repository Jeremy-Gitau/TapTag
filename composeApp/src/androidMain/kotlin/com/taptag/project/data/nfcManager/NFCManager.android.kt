package com.taptag.project.data.nfcManager

import android.app.Activity
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.nio.charset.Charset

private const val TAG = "NFCManager"
private const val READER_PRESENCE_CHECK_DELAY = 500
private const val ULTRALIGHT_PAGES_TO_READ = 4

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual open class NFCManager : NfcAdapter.ReaderCallback {

    private var nfcAdapter: NfcAdapter? = null
    private val _tagData = MutableSharedFlow<String>()
    private val _writeResult = MutableSharedFlow<Boolean>()
    private val scope = CoroutineScope(SupervisorJob())
    actual val tags: SharedFlow<String> = _tagData
    actual val writeResult: SharedFlow<Boolean> = _writeResult

    // Data to write to the next detected tag
    private var pendingWriteData: String? = null
    private var isWriteMode: Boolean = false

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
        Log.d(TAG, "Write mode is: $isWriteMode, pending data: $pendingWriteData")

        // If in write mode, attempt to write data to the tag
        if (isWriteMode == true && pendingWriteData != null) {
            Log.d(TAG, "Attempting to write data to tag")
            val success = writeToTag(tag, pendingWriteData.toString())
            scope.launch {
                _writeResult.emit(success)
            }

            // Reset write mode after attempt
            if (success) {
                scope.launch {
                    isWriteMode = false
                    pendingWriteData = ""
                }

            }
            return
        }

        // Otherwise, read the tag
        val ndefMessage = readNdefMessage(tag)

        if (ndefMessage != null) {
            processNdefMessage(ndefMessage)
        } else {
            handleRawTag(tag)
        }
    }

    /**
     * Prepares the manager to write data to the next detected tag
     * @param data The string data to write to the tag
     */
    actual fun prepareWrite(data: String) {

        scope.launch {
            pendingWriteData = data
            isWriteMode = true
        }

        Log.d(TAG, "Prepared to write data: $data, isWriteMode set to: $isWriteMode")
    }

    /**
     * Cancels any pending write operation
     */
    actual fun cancelWrite() {

        scope.launch {
            pendingWriteData = ""
            isWriteMode = false
        }

        Log.d(TAG, "Write operation cancelled")
    }

    /**
     * Writes data to an NFC tag
     * @param tag The tag to write to
     * @param data The string data to write
     * @return True if write was successful, false otherwise
     */
    private fun writeToTag(tag: Tag, data: String): Boolean {
        // Try writing as NDEF first (most compatible)
        if (writeNdefToTag(tag, data)) {
            return true
        }

        // Try writing to Mifare Ultralight if NDEF failed
        if (writeMifareUltralight(tag, data)) {
            return true
        }

        // Try formatting and writing if all else fails
        return formatAndWriteNdef(tag, data)
    }

    /**
     * Writes data as an NDEF message to a tag
     */
    private fun writeNdefToTag(tag: Tag, data: String): Boolean {
        val ndef = Ndef.get(tag) ?: return false

        return try {
            ndef.connect()

            if (!ndef.isWritable) {
                Log.w(TAG, "Tag is read-only")
                return false
            }

            // Create an NDEF record with the data
            val record = NdefRecord.createTextRecord("en", data)
            val message = NdefMessage(arrayOf(record))

            // Check if the message fits on the tag
            if (message.byteArrayLength > ndef.maxSize) {
                Log.w(TAG, "Message too large for tag capacity")
                return false
            }

            ndef.writeNdefMessage(message)
            Log.d(TAG, "Successfully wrote NDEF message to tag")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing NDEF message", e)
            false
        } finally {
            try {
                ndef.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing NDEF connection", e)
            }
        }
    }

    /**
     * Writes data to a Mifare Ultralight tag directly
     */
    private fun writeMifareUltralight(tag: Tag, data: String): Boolean {
        if (!tag.techList.contains(MifareUltralight::class.java.name)) {
            return false
        }

        val ultralight = MifareUltralight.get(tag)
        return try {
            ultralight.connect()

            // Convert string to bytes
            val dataBytes = data.toByteArray(Charset.forName("UTF-8"))

            // Mifare Ultralight page is 4 bytes
            val pageSize = 4

            // Start writing at page 4 (user data starts here)
            val startPage = 4

            // Calculate how many pages we need
            val pages = (dataBytes.size + pageSize - 1) / pageSize

            for (i in 0 until pages) {
                val pageData = ByteArray(pageSize)
                val offset = i * pageSize
                val length = kotlin.math.min(pageSize, dataBytes.size - offset)

                if (length > 0) {
                    System.arraycopy(dataBytes, offset, pageData, 0, length)
                }

                ultralight.writePage(startPage + i, pageData)
            }

            Log.d(TAG, "Successfully wrote data to Mifare Ultralight")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error writing to Mifare Ultralight", e)
            false
        } finally {
            try {
                ultralight.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing Mifare Ultralight connection", e)
            }
        }
    }

    /**
     * Attempts to format a tag and write NDEF data to it
     */
    private fun formatAndWriteNdef(tag: Tag, data: String): Boolean {
        val format = NdefFormatable.get(tag) ?: return false

        return try {
            format.connect()

            // Create NDEF message
            val record = NdefRecord.createTextRecord("en", data)
            val message = NdefMessage(arrayOf(record))

            // Format tag and write message
            format.format(message)

            Log.d(TAG, "Successfully formatted and wrote to tag")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting tag", e)
            false
        } finally {
            try {
                format.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing NdefFormatable connection", e)
            }
        }
    }

    private fun readNdefMessage(tag: Tag): NdefMessage? {
        val mNdef = Ndef.get(tag) ?: return null

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
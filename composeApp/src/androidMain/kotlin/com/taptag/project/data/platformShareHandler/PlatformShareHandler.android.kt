@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.platformShareHandler

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri


class AndroidShareHandler(private val context: Context) : PlatformShareHandler {

    override fun sendEmail(recipient: String) {

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        }

        try {
            context.startActivity(Intent.createChooser(intent, "send email using..."))
        } catch (e: Exception) {
            Log.d("sendEmail():", e.message.toString())

            Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
        }

    }

    override fun sendSms(phoneNumber: String) {

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.d("sendSms():", e.message.toString())

            Toast.makeText(context, "No messaging app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun openWhatsApp(phoneNumber: String) {

        try {

            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://api.whatsapp.com/send?phone=$phoneNumber".toUri()
            }

            context.startActivity(intent)

        } catch (e: Exception) {
            Log.d("openWhatsApp():", e.message.toString())

            Toast.makeText(context, "Error Opening WhatsApp", Toast.LENGTH_SHORT).show()
        }


    }

    override fun shareText(text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Share via..."))
        } catch (e: Exception) {

            Log.d("shareText():", e.message.toString())

            Toast.makeText(context, "No sharing apps found", Toast.LENGTH_SHORT).show()
        }

    }

}

@Composable
actual fun getPlatformShareHandler(): PlatformShareHandler {

    return AndroidShareHandler(LocalContext.current)
}
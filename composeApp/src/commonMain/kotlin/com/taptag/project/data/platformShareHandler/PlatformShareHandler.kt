@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.platformShareHandler

import androidx.compose.runtime.Composable

interface PlatformShareHandler {

    fun sendEmail(recipient: String)

    fun sendSms(phoneNumber: String)

    fun openWhatsApp(phoneNumber: String)

    fun shareText(text: String)
}

@Composable
expect fun getPlatformShareHandler(): PlatformShareHandler

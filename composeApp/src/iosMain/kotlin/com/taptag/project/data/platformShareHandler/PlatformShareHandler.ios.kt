@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.platformShareHandler

import androidx.compose.runtime.Composable


class IosShareHandler: PlatformShareHandler{

    override fun SendEmail(recipient: String) {
        TODO("Not yet implemented")
    }

    override fun SendSms(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun OpenWhatsApp(phoneNumber: String) {
        TODO("Not yet implemented")
    }

    override fun ShareText(text: String) {
        TODO("Not yet implemented")
    }


}

@Composable
actual fun getPlatformShareHandler(): PlatformShareHandler = IosShareHandler()
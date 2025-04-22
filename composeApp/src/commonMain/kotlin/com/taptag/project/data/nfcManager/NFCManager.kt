@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package com.taptag.project.data.nfcManager

import androidx.compose.runtime.Composable
import com.taptag.project.domain.models.ContactDomain
import kotlinx.coroutines.flow.SharedFlow

expect open class NFCManager {

    val tags: SharedFlow<String>

    val writeResult: SharedFlow<Boolean>

    val contacts: SharedFlow<ContactDomain>

    @Composable
    fun RegisterApp()

    fun prepareWrite(data: String)

    fun prepareWriteContact(contact: ContactDomain)

    fun cancelWrite()

}

@Composable
expect fun getNFCManager(): NFCManager
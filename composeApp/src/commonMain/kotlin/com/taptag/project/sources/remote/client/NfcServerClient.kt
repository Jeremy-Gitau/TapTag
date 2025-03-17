package com.taptag.project.sources.remote.client

import com.taptag.project.sources.remote.dtos.ContactsRequestData
import com.taptag.project.sources.remote.dtos.ContactsResponseData
import com.taptag.project.sources.remote.dtos.PaymentsRequestData
import com.taptag.project.sources.remote.dtos.AuthRequestData
import com.taptag.project.sources.remote.dtos.AuthResponseData
import com.taptag.project.sources.remote.dtos.ContactData
import com.taptag.project.sources.remote.helpers.NetworkResult

interface NfcServerClient {

    suspend fun registerUser(data: AuthRequestData): NetworkResult<AuthResponseData>
    suspend fun loginUser(data: AuthRequestData): NetworkResult<AuthResponseData>
    suspend fun saveNewContact(data: ContactsRequestData): NetworkResult<ContactData>
    suspend fun getAllContacts(): NetworkResult<List<ContactData>>
    suspend fun initiatePayments(data: PaymentsRequestData)

}
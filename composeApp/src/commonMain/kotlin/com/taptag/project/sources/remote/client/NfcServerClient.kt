package com.taptag.project.sources.remote.client

import com.taptag.project.sources.remote.dtos.AuthRequestData
import com.taptag.project.sources.remote.dtos.AuthResponseData
import com.taptag.project.sources.remote.dtos.ChangePasswordRequestData
import com.taptag.project.sources.remote.dtos.ContactData
import com.taptag.project.sources.remote.dtos.ContactsRequestData
import com.taptag.project.sources.remote.dtos.PaymentsRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenResponseData
import com.taptag.project.sources.remote.dtos.UserProfileDto
import com.taptag.project.sources.remote.helpers.NetworkResult

interface NfcServerClient {

    suspend fun registerUser(data: AuthRequestData): NetworkResult<AuthResponseData>

    suspend fun loginUser(data: AuthRequestData): NetworkResult<AuthResponseData>

    suspend fun logout(token: String): NetworkResult<Boolean>

    suspend fun saveNewContact(data: ContactsRequestData, token: String):
            NetworkResult<ContactData>

    suspend fun getAllContacts(token: String): NetworkResult<List<ContactData>>

    suspend fun updateContact(data: ContactsRequestData, id: String):
            NetworkResult<ContactData>

    suspend fun deleteContact(id: String): NetworkResult<Boolean>

    suspend fun refreshToken(data: RefreshTokenRequestData):
            NetworkResult<RefreshTokenResponseData>

    suspend fun changePassword(data: ChangePasswordRequestData): NetworkResult<Boolean>

    suspend fun forgotPassword(email: String): NetworkResult<Boolean>

    suspend fun checkExistingUser(email: String): NetworkResult<Boolean>

    suspend fun initiatePayments(data: PaymentsRequestData)

    suspend fun saveUserProfile(
        token: String,
        data: UserProfileDto
    ): NetworkResult<UserProfileDto>

    suspend fun fetchUserProfile(token: String): NetworkResult<UserProfileDto>

    suspend fun updateUserProfile(id: String, data: UserProfileDto): NetworkResult<UserProfileDto>

    suspend fun deleteUserProfile(id: String): NetworkResult<Boolean>

}
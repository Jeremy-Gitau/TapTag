package com.taptag.project.sources.remote.client

import com.taptag.project.sources.remote.dtos.ContactsRequestData
import com.taptag.project.sources.remote.dtos.ContactsResponseData
import com.taptag.project.sources.remote.dtos.PaymentsRequestData
import com.taptag.project.sources.remote.dtos.AuthRequestData
import com.taptag.project.sources.remote.dtos.AuthResponseData
import com.taptag.project.sources.remote.dtos.ContactData
import com.taptag.project.sources.remote.helpers.Endpoints
import com.taptag.project.sources.remote.helpers.NetworkResult
import com.taptag.project.sources.remote.helpers.safeApiCall
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class NfcServerClientImpl(
    private val client: HttpClient
) : NfcServerClient {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun registerUser(data: AuthRequestData): NetworkResult<AuthResponseData> =
        safeApiCall {

            println("registered client data: $data")

            val responseData: HttpResponse = client.post(
                Endpoints.RegisterUser.url
            ) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(data)
            }

            println("user response data from client: $responseData")

            if (responseData.status.value != 201) {
                val errorText = responseData.bodyAsText()
                NetworkResult.Error("Server error: ${responseData.status.value} - $errorText")
            }

            val userResponse: AuthResponseData = json.decodeFromString(
                responseData.bodyAsText()
            )

            client.close()

            println("registered User: $userResponse")

            userResponse
        }

    override suspend fun loginUser(data: AuthRequestData): NetworkResult<AuthResponseData> =
        safeApiCall {

            val response: HttpResponse = client.post(
                Endpoints.LoginUser.url
            ) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(data)
            }

            val userResponse: AuthResponseData = json.decodeFromString(
                response.bodyAsText()
            )

            client.close()

            userResponse
        }

    override suspend fun saveNewContact(
        data: ContactsRequestData,
        token: String
    ): NetworkResult<ContactData> =
        safeApiCall {

            val response: HttpResponse = client.post(
                Endpoints.NewContact.url
            ) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(token = token)
                setBody(data)
            }

            val contactResponse: ContactData = json.decodeFromString(
                response.bodyAsText()
            )

            client.close()

            contactResponse
        }

    override suspend fun getAllContacts(token: String): NetworkResult<List<ContactData>> =
        safeApiCall {

            val response: HttpResponse = client.get(
                Endpoints.GetAllContacts.url
            ) {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                bearerAuth(token = token)
            }

            val contactResponse: List<ContactData> = json.decodeFromString(
                response.bodyAsText()
            )

            client.close()

            contactResponse
        }

    override suspend fun initiatePayments(data: PaymentsRequestData) {

        val response: HttpResponse = client.post(
            Endpoints.InitiatePayment.url
        ) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            setBody(data)
        }

//        val paymentResponse: PaymentsResponseData = json.decodeFromString(
//            response.bodyAsText()
//        )
//
        client.close()

//        paymentResponse
    }
}
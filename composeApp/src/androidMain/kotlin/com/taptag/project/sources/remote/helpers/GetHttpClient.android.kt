package com.taptag.project.sources.remote.helpers

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun getHttpClient(): HttpClient {
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                json = Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                }
            )
        }

        install(DefaultRequest) {
            header(HttpHeaders.Accept, ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
}
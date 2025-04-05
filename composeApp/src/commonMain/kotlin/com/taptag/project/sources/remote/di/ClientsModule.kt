package com.taptag.project.sources.remote.di

import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.client.NfcServerClientImpl
import com.taptag.project.sources.remote.helpers.getHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val clientsModule = module {

    single {
        HttpClient {
            install(ContentNegotiation){
                json(json = Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }
        }
    }

    single<HttpClient> { getHttpClient() }
    single<NfcServerClient> { NfcServerClientImpl(client = get()) }

}
package com.taptag.project.sources.remote.helpers

import androidx.compose.ui.platform.InspectableModifier

sealed class Endpoints(private val path: String) {

    private val baseUrl = "https://nfc-contact-app-06c90874a73f.herokuapp.com/api/"

    val url: String
        get() = "$baseUrl$path"

    data object RegisterUser: Endpoints("auth/register/")

    data object LoginUser: Endpoints("auth/login")

    data object LogOut: Endpoints("auth/logout")

    data object GetAllContacts: Endpoints("contacts")

    data object NewContact: Endpoints("contacts")

    data class UpdateContact(val id: String): Endpoints("contacts/$id")

    data class DeleteContact(val id: String): Endpoints("contacts/$id")

    data object RefreshToken: Endpoints("auth/refresh")

    data object InitiatePayment: Endpoints("payments/initiate")

}
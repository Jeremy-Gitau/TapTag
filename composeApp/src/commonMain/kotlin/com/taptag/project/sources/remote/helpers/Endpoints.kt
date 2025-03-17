package com.taptag.project.sources.remote.helpers

sealed class Endpoints(private val path: String) {

    private val baseUrl = "http://localhost:5000/api/"

    val url: String
        get() = "$baseUrl$path"

    data object RegisterUser: Endpoints("auth/register/")

    data object LoginUser: Endpoints("auth/login")

    data object GetAllContacts: Endpoints("contacts")

    data object NewContact: Endpoints("contacts")

    data object InitiatePayment: Endpoints("payments/initiate")

}
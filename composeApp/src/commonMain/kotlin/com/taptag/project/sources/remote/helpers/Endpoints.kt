package com.taptag.project.sources.remote.helpers

sealed class Endpoints(private val path: String) {

    private val baseUrl = "https://nfc-contact-app-06c90874a73f.herokuapp.com/api/"

    val url: String
        get() = "$baseUrl$path"

    data object RegisterUser : Endpoints("auth/register/")

    data object LoginUser : Endpoints("auth/login")

    data object LogOut : Endpoints("auth/logout")

    data object Contact : Endpoints("contacts")

    data class ContactWithId(val id: String) : Endpoints("contacts/$id")

    data object RefreshToken : Endpoints("auth/refresh-token")

    data object InitiatePayment : Endpoints("payments/initiate")

    data object ChangePassword : Endpoints("auth/change-password")

    data object CheckExistingUser : Endpoints("auth/check-existing-user")

    data object ForgotPassword : Endpoints("auth/forgot-password")

    data object UserProfile : Endpoints("auth/profile")

    data class ProfileWithId(val id: String) : Endpoints("auth/profile/$id")

}
package com.taptag.project.domain.models

data class AuthRequestDomain(
    val firstName: String? = null,
    val secondName: String? = null,
    val email: String,
    val workEmail: String? = null,
    val company: String? = null,
    val password: String,
    val confirmPassword: String? = null
)

data class AuthResponseDomain(
    val accessToken: String,
    val refreshToken: String,
    val user: CurrentUserDomain
)

data class CurrentUserDomain(
    val id: String,
    val firstName: String,
    val secondName: String,
    val email: String
)
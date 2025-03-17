package com.taptag.project.domain.models

data class UserRequestDomain(
    val name: String? = null,
    val email: String,
    val password: String
)

data class UserResponseDomain(
    val token: String,
    val user: CurrentUserDomain
)

data class CurrentUserDomain(
    val id: String,
    val name: String,
    val email: String
)
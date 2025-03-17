package com.taptag.project.sources.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestData(
    val name: String? = null,
    val email: String,
    val password: String
)

@Serializable
data class AuthResponseData(
    val token: String,
    val user: UserData
)

@Serializable
data class UserData(
    val id: String,
    val name: String,
    val email: String
)
package com.taptag.project.sources.remote.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequestData(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String,
    val workEmail: String? = null,
    val company: String? = null,
    val password: String,
    val confirmPassword: String? = null
)

@Serializable
data class AuthResponseData(
    val accessToken: String,
    val refreshToken: String,
    val user: UserData
)

@Serializable
data class UserData(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

@Serializable
data class RefreshTokenRequestData(
    val refreshToken: String
)

@Serializable
data class RefreshTokenResponseData(
    val accessToken: String,
    val refreshToken: String
)
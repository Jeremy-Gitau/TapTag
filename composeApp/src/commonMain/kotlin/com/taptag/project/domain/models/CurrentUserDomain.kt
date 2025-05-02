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
    val accessToken: String = "",
    val refreshToken: String = "",
    val user: CurrentUserDomain = CurrentUserDomain()
)

data class CurrentUserDomain(
    val id: String = "",
    val firstName: String = "",
    val secondName: String = "",
    val email: String = ""
)

data class RefreshTokenRequestDomain(
    val refreshToken: String
)

data class RefreshTokenResponseDomain(
    val accessToken: String,
    val refreshToken: String
)

data class ChangePasswordRequestDomain(
    val oldPassword: String,
    val newPassword: String
)

data class UserProfileDomain(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val workEmail: String? = null,
    val company: String? = null,
    val role: String = "",
    val status: String = "",
    val profilePicture: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
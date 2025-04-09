package com.taptag.project.data.mappers

import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.CurrentUserDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.sources.remote.dtos.AuthRequestData
import com.taptag.project.sources.remote.dtos.AuthResponseData
import com.taptag.project.sources.remote.dtos.RefreshTokenRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenResponseData
import com.taptag.project.sources.remote.dtos.UserData

fun AuthResponseData.toDomain() = AuthResponseDomain(
    accessToken = this.accessToken,
    refreshToken = this.refreshToken,
    user = this.user.toDomain()
)

fun UserData.toDomain() = CurrentUserDomain(
    id = this.id,
    firstName = this.firstName,
    secondName = this.lastName,
    email = this.email,
)

fun AuthRequestDomain.toDto() = AuthRequestData(
    firstName = this.firstName,
    lastName = this.secondName,
    email = this.email,
    workEmail = this.workEmail,
    company = this.company,
    password = this.password,
    confirmPassword = this.confirmPassword
)

fun RefreshTokenResponseData.toDomain() =
    RefreshTokenResponseDomain(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken
    )

fun RefreshTokenRequestDomain.toDto() = RefreshTokenRequestData(
    refreshToken = this.refreshToken
)


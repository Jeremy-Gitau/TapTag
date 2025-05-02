package com.taptag.project.data.mappers

import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.ChangePasswordRequestDomain
import com.taptag.project.domain.models.CurrentUserDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.domain.models.UserProfileDomain
import com.taptag.project.sources.local.room.entities.UserEntity
import com.taptag.project.sources.local.room.entities.UserProfileEntity
import com.taptag.project.sources.remote.dtos.AuthRequestData
import com.taptag.project.sources.remote.dtos.AuthResponseData
import com.taptag.project.sources.remote.dtos.ChangePasswordRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenResponseData
import com.taptag.project.sources.remote.dtos.UserData
import com.taptag.project.sources.remote.dtos.UserProfileDto

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

fun AuthResponseData.toEntity() = UserEntity(
    id = this.user.id,
    firstName = this.user.firstName,
    lastName = this.user.lastName,
    email = this.user.email,
    accessToken = this.accessToken,
    refreshToken = this.refreshToken
)

fun UserEntity.toDomain(): AuthResponseDomain = AuthResponseDomain(
        accessToken = this.accessToken,
        refreshToken = this.refreshToken,
        user = CurrentUserDomain(
            id = this.id,
            firstName = this.firstName,
            secondName = this.lastName,
            email = this.email
        )
    )

fun AuthResponseData.toProfileEntity() = UserProfileEntity(
    id = this.user.id,
    firstName = this.user.firstName,
    lastName = this.user.lastName,
    email = this.user.email,
)

fun UserProfileDto.toProfileEntity() = UserProfileEntity(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    workEmail = this.workEmail,
    company = this.company,
    role = this.role,
    status = this.status,
    profilePicture = this.profilePicture,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun UserProfileEntity.toProfileDomain() = UserProfileDomain(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    workEmail = this.workEmail,
    company = this.company,
    role = this.role,
    status = this.status,
    profilePicture = this.profilePicture,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun ChangePasswordRequestDomain.toDto() = ChangePasswordRequestData(
    newPassword = this.newPassword,
    oldPassword = this.oldPassword
)

fun UserProfileDomain.toDto() = UserProfileDto(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    workEmail = this.workEmail,
    company = this.company,
    role = this.role,
    status = this.status,
    profilePicture = this.profilePicture,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)

fun UserProfileDto.toDomain() = UserProfileDomain(
    id = this.id,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    workEmail = this.workEmail,
    company = this.company,
    role = this.role,
    status = this.status,
    profilePicture = this.profilePicture,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)


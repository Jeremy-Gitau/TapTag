package com.taptag.project.data.mappers

import com.taptag.project.domain.models.CurrentUserDomain
import com.taptag.project.domain.models.UserRequestDomain
import com.taptag.project.domain.models.UserResponseDomain
import com.taptag.project.sources.remote.dtos.UserData
import com.taptag.project.sources.remote.dtos.AuthRequestData
import com.taptag.project.sources.remote.dtos.AuthResponseData

fun AuthResponseData.toDomain() = UserResponseDomain(
    token = this.token,
    user = this.user.toDomain()
)

fun UserData.toDomain() = CurrentUserDomain(
    id = this.id,
    name = this.name,
    email = this.email
)

fun UserRequestDomain.toDto() = AuthRequestData(
    name = this.name,
    email = this.email,
    password = this.password
)


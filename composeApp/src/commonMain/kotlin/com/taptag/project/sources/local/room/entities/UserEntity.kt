package com.taptag.project.sources.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class AuthResponseEntity(
    val accessToken: String,
    val refreshToken: String,
    val user: UserEntity
)

@Entity
data class UserEntity(
    @PrimaryKey
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

@Entity
data class RefreshTokenRequestEntity(
    val refreshToken: String
)

@Entity
data class RefreshTokenResponseEntity(
    val accessToken: String,
    val refreshToken: String
)
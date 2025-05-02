package com.taptag.project.sources.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userProfile")
data class UserProfileEntity(
    @PrimaryKey
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
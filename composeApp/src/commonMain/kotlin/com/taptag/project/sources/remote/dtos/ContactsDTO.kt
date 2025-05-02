package com.taptag.project.sources.remote.dtos

import com.taptag.project.domain.models.ContactStatus
import kotlinx.serialization.Serializable

@Serializable
data class ContactsRequestData(
    val name: String,
    val email: String,
    val phone: String,
    val notes: String,
    val company: String,
    val tags: List<String>
)

@Serializable
data class ContactData(
    val id: Int = 0,
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val profession: String = "",
    val role: String = "",
    val company: String = "",
    val status: ContactStatus = ContactStatus.PENDING,
    val avatarUrl: String = "",
    val tags: List<String> = emptyList(),
    val notes: String = "",
)

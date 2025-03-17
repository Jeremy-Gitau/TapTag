package com.taptag.project.sources.remote.dtos

import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.domain.models.EventType
import com.taptag.project.domain.models.RelationshipStrength
import kotlinx.serialization.Serializable

@Serializable
data class ContactsRequestData(
    val name: String,
    val email: String,
    val notes: String
)

@Serializable
data class ContactsResponseData(
    val name: String,
    val email: String,
    val notes: String,
    val userId: String,
    val _id: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)

@Serializable
data class ContactData(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    val company: String,
    val lastContact: String,
    val status: ContactStatus,
    val avatarUrl: String,
    val relationshipStrength: RelationshipStrength,
    val tags: List<String>,
    val notes: String,
    val nextFollowUp: String?,
    val scheduledDate: String?,
    val history: List<ContactEventEntity>
)

@Serializable
data class ContactEventEntity(
    val date: String,
    val type: EventType,
    val details: String
)
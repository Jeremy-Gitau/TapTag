package com.taptag.project.data.mappers

import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactEvent
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.models.ContactsResponseDomain
import com.taptag.project.sources.remote.dtos.ContactData
import com.taptag.project.sources.remote.dtos.ContactEventEntity
import com.taptag.project.sources.remote.dtos.ContactsRequestData
import com.taptag.project.sources.remote.dtos.ContactsResponseData

fun ContactsResponseData.toDomain() = ContactsResponseDomain(
    name = this.name,
    email = this.email,
    notes = this.notes,
    userId = this.userId,
    _id = this._id,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    __v = this.__v
)

fun ContactsRequestDomain.toDto() = ContactsRequestData(
    name = this.name,
    email = this.email,
    notes = this.notes
)

fun ContactData.toDomain() = ContactDomain(
    name = this.name,
    notes = this.notes,
    avatarUrl = this.avatarUrl,
    company = this.company,
    history = this.history.map { it.toDomain() },
    id = this.id,
    lastContact = this.lastContact,
    tags = this.tags,
    role = this.role,
    nextFollowUp = this.nextFollowUp,
    relationshipStrength = this.relationshipStrength,
    scheduledDate = this.scheduledDate,
    status = this.status,
    email = this.email
)

fun ContactEventEntity.toDomain() = ContactEvent(
    date = this.date,
    type = this.type,
    details = this.details
)
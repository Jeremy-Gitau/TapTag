package com.taptag.project.data.mappers

import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.sources.local.room.entities.ContactEntity
import com.taptag.project.sources.remote.dtos.ContactData
import com.taptag.project.sources.remote.dtos.ContactsRequestData

fun ContactsRequestDomain.toDto() = ContactsRequestData(
    name = this.name,
    email = this.email,
    notes = this.notes,
    phone = this.phone,
    company = this.company,
    tags = this.tags
)

fun ContactData.toDomain() = ContactDomain(
    userId = this.userId,
    name = this.name,
    notes = this.notes,
    avatarUrl = this.avatarUrl,
    company = this.company,
    id = this.id,
    tags = this.tags,
    role = this.role,
    profession = this.profession,
    status = this.status,
    email = this.email,
)

fun ContactData.toEntity() = ContactEntity(
    userId = this.userId,
    name = this.name,
    notes = this.notes,
    avatarUrl = this.avatarUrl,
    company = this.company,
    id = this.id,
    tags = this.tags,
    role = this.role,
    profession = this.profession,
    status = this.status,
    email = this.email,
)

fun ContactEntity.toDomain() = ContactDomain(
    userId = this.userId,
    name = this.name,
    notes = this.notes,
    avatarUrl = this.avatarUrl,
    company = this.company,
    id = this.id,
    tags = this.tags,
    role = this.role,
    profession = this.profession,
    status = this.status,
    email = this.email,
)

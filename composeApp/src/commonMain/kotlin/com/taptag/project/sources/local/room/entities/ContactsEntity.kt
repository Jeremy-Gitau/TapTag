package com.taptag.project.sources.local.room.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.domain.models.EventType
import com.taptag.project.domain.models.RelationshipStrength


@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey val id: Int,
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
//    @Embedded(prefix = ("contact_event_type")) val history: List<ContactEventEntity>
)

@Entity
data class ContactEventEntity(
    val date: String,
    val type: EventType,
    val details: String
)
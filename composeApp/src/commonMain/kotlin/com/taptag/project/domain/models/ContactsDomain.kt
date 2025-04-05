package com.taptag.project.domain.models

data class ContactsRequestDomain(
    val name: String,
    val email: String,
    val notes: String
)

data class ContactsResponseDomain(
    val id: String,
    val name: String,
    val email: String,
    val notes: String,
    val userId: String,
    val createdAt: String,
    val updatedAt: String,
)


data class ContactDomain(
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
    val history: List<ContactEvent>
)

data class ContactEvent(
    val date: String,
    val type: EventType,
    val details: String
)

enum class ContactStatus {
    PENDING,
    FOLLOWED_UP,
    SCHEDULED;

    fun getLabel(): String = when(this) {
        PENDING -> "Need Follow-up"
        FOLLOWED_UP -> "Followed Up"
        SCHEDULED -> "Meeting Set"
    }
}

enum class RelationshipStrength {
    NEW,
    DEVELOPING,
    STRONG
}

enum class EventType {
    MEETING,
    EMAIL,
    CALL,
    INTRO,
    MESSAGE
}
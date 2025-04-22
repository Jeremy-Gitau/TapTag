package com.taptag.project.domain.models

data class ContactsRequestDomain(
    val name: String,
    val email: String,
    val phone: String,
    val notes: String,
    val company: String,
    val tags: List<String>
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
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val company: String = "",
    val lastContact: String = "",
    val status: ContactStatus = ContactStatus.PENDING,
    val avatarUrl: String = "",
    val relationshipStrength: RelationshipStrength = RelationshipStrength.NEW,
    val tags: List<String> = emptyList(),
    val notes: String = "",
    val nextFollowUp: String? = null,
    val scheduledDate: String? = null,
    val history: List<ContactEvent> = emptyList()
)

data class ContactEvent(
    val date: String = "",
    val type: EventType = EventType.MEETING,
    val details: String = ""
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
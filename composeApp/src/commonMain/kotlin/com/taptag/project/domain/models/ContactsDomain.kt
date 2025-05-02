package com.taptag.project.domain.models

data class ContactsRequestDomain(
    val name: String,
    val email: String,
    val phone: String,
    val notes: String,
    val company: String,
    val tags: List<String>
)

data class ContactDomain(
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


enum class ContactStatus {
    PENDING,
    FOLLOWED_UP,
    SCHEDULED;

    fun getLabel(): String = when (this) {
        PENDING -> "Need Follow-up"
        FOLLOWED_UP -> "Followed Up"
        SCHEDULED -> "Meeting Set"
    }
}

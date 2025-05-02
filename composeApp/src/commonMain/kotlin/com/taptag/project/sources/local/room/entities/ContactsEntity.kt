package com.taptag.project.sources.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taptag.project.domain.models.ContactStatus
import io.ktor.utils.io.bits.of
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime


@Entity(tableName = "contact")
data class ContactEntity(
    @PrimaryKey
    val id: Int,
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profession: String = "",
    val role: String = "",
    val company: String = "",
    val status: ContactStatus = ContactStatus.PENDING,
    val avatarUrl: String = "",
    val tags: List<String> = emptyList(),
    val notes: String = ""
)

//    val lastUpdated: String = "",
//    val createdAt: String = "",
//    val syncPending: Boolean = false

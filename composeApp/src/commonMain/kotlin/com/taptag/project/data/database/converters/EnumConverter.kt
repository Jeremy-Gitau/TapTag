package com.taptag.project.data.database.converters

import androidx.room.TypeConverter
import com.taptag.project.domain.models.ContactStatus
import com.taptag.project.domain.models.EventType
import com.taptag.project.domain.models.RelationshipStrength

class EnumConverter {
    @TypeConverter
    fun fromContactStatus(value: ContactStatus): String {
        return value.name
    }

    @TypeConverter
    fun toContactStatus(value: String): ContactStatus {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromRelationshipStrength(value: RelationshipStrength): String {
        return value.name
    }

    @TypeConverter
    fun toRelationshipStrength(value: String): RelationshipStrength {
        return enumValueOf(value)
    }
}
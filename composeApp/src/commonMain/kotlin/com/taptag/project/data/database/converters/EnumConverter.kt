package com.taptag.project.data.database.converters

import androidx.room.TypeConverter
import com.taptag.project.domain.models.ContactStatus

class EnumConverter {
    @TypeConverter
    fun fromContactStatus(value: ContactStatus): String {
        return value.name
    }

    @TypeConverter
    fun toContactStatus(value: String): ContactStatus {
        return enumValueOf(value)
    }

}
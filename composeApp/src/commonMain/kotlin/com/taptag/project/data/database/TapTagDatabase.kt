package com.taptag.project.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.taptag.project.data.dao.ContactDao
import com.taptag.project.data.dao.UserDao
import com.taptag.project.data.database.converters.EnumConverter
import com.taptag.project.data.database.converters.StringListConverter
import com.taptag.project.sources.local.room.entities.ContactEntity
import com.taptag.project.sources.local.room.entities.UserEntity

@Database(
    entities = [UserEntity::class, ContactEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class, EnumConverter::class)
@ConstructedBy(TapTagDatabaseConstructor::class)
abstract class TapTagDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
}

@Suppress("KotlinNoActualForExpect")
expect object TapTagDatabaseConstructor : RoomDatabaseConstructor<TapTagDatabase> {
    override fun initialize(): TapTagDatabase
}

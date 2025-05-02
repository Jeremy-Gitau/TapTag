package com.taptag.project.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.taptag.project.data.dao.ContactDao
import com.taptag.project.data.dao.UserDao
import com.taptag.project.data.dao.UserProfileDao
import com.taptag.project.data.database.converters.EnumConverter
import com.taptag.project.data.database.converters.StringListConverter
import com.taptag.project.sources.local.room.entities.ContactEntity
import com.taptag.project.sources.local.room.entities.UserEntity
import com.taptag.project.sources.local.room.entities.UserProfileEntity

val DATABASE_MIGRATION = object : Migration(1,7) {

    override fun migrate(database: SQLiteConnection) {
        // Create a new table with the updated schema
        database.execSQL(
            "CREATE TABLE contact_new (" +
                    "id INTEGER PRIMARY KEY NOT NULL, " +
                    "userId TEXT NOT NULL, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT NOT NULL, " +
                    "phone TEXT NOT NULL DEFAULT '', " +
                    "profession TEXT NOT NULL, " +
                    "role TEXT NOT NULL, " +
                    "company TEXT NOT NULL, " +
                    "status TEXT NOT NULL, " +
                    "avatarUrl TEXT NOT NULL, " +
                    "tags TEXT NOT NULL, " +
                    "notes TEXT NOT NULL)"
        )
        database.execSQL(
            "CREATE TABLE userProfile (" +
                    "id TEXT PRIMARY KEY NOT NULL , " +
                    "firstName TEXT NOT NULL, " +
                    "lastName TEXT NOT NULL , " +
                    "email TEXT NOT NULL , " +
                    "workEmail TEXT  , " +
                    "company TEXT  , " +
                    "role TEXT NOT NULL  , " +
                    "status TEXT NOT NULL , " +
                    "profilePicture TEXT NOT NULL, " +
                    "createdAt TEXT NOT NULL, " +
                    "updatedAt TEXT NOT NULL )"
        )

        // Copy data from the old table to the new one, using defaults for new columns
        database.execSQL(
            "INSERT INTO contact_new (id, userId, name,profession, email, role, company, status, avatarUrl, tags, notes) " +
                    "SELECT id, userId, name,profession, email, role, company, status, avatarUrl, tags, notes FROM contact"
        )

        // Drop the old table
        database.execSQL("DROP TABLE contact")

        // Rename the new table to the correct name
        database.execSQL("ALTER TABLE contact_new RENAME TO contact")
    }

}

@Database(
    entities = [UserEntity::class, ContactEntity::class, UserProfileEntity::class],
    version = 7,
    exportSchema = true
)
@TypeConverters(StringListConverter::class, EnumConverter::class)
@ConstructedBy(TapTagDatabaseConstructor::class)
abstract class TapTagDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun contactDao(): ContactDao
    abstract fun userProfileDao(): UserProfileDao
}

@Suppress("KotlinNoActualForExpect")
expect object TapTagDatabaseConstructor : RoomDatabaseConstructor<TapTagDatabase> {
    override fun initialize(): TapTagDatabase
}

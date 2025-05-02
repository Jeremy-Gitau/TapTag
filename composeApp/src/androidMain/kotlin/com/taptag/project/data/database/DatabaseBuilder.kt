package com.taptag.project.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<TapTagDatabase> {

    val dbFile = context.getDatabasePath("tap_tag.db")

    return Room.databaseBuilder(
        context = context,
        name = dbFile.absolutePath,
        klass = TapTagDatabase::class.java
    )
        .addMigrations(DATABASE_MIGRATION)
}
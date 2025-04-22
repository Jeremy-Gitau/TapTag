package com.taptag.project.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.taptag.project.sources.local.room.entities.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContact(contact: ContactEntity)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Transaction
    @Query("SELECT * FROM contact WHERE id = :contactId")
    suspend fun getContactById(contactId: Int): ContactEntity?

    @Transaction
    @Query("SELECT * FROM contact WHERE id = :contactId")
    fun getContactByIdFlow(contactId: Int): Flow<ContactEntity?>

    @Transaction
    @Query("DELETE FROM contact WHERE id = :contactId")
    suspend fun deleteContactById(contactId: Int)

}
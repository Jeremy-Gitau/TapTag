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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllContacts(contacts: List<ContactEntity>)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Transaction
    @Query("SELECT * FROM contact WHERE id = :contactId")
    suspend fun getContactById(contactId: Int): ContactEntity?

    @Transaction
    @Query("SELECT * FROM contact WHERE id = :contactId")
    fun getContactByIdFlow(contactId: Int): Flow<ContactEntity?>

    @Transaction
    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): List<ContactEntity>

    @Transaction
    @Query("SELECT * FROM contact")
    fun getAllContactsFlow(): Flow<List<ContactEntity>>

    @Transaction
    @Query("DELETE FROM contact WHERE id = :contactId")
    suspend fun deleteContactById(contactId: Int)

    @Transaction
    @Query("DELETE FROM contact")
    suspend fun deleteAllContacts()

    @Transaction
    @Query("SELECT COUNT(*) FROM contact")
    suspend fun getContactCount(): Int

//    @Transaction
//    @Query("SELECT * FROM contact WHERE syncPending = 1")
//    suspend fun getPendingSyncContacts(): List<ContactEntity>
//
//    @Transaction
//    @Query("UPDATE contact SET syncPending = 0 WHERE id = :contactId")
//    suspend fun markContactSynced(contactId: Int)
}
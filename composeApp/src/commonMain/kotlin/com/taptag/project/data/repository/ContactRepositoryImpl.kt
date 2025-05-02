package com.taptag.project.data.repository

import com.taptag.project.data.database.TapTagDatabase
import com.taptag.project.data.mappers.toDomain
import com.taptag.project.data.mappers.toDto
import com.taptag.project.data.mappers.toEntity
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.domain.repository.ContactsRepository
import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.helpers.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val TAG = "ContactRepositoryImpl"


class ContactRepositoryImpl(
    private val client: NfcServerClient,
    private val database: TapTagDatabase
) : ContactsRepository {

    override suspend fun saveNewContact(
        data: ContactsRequestDomain,
        token: String
    ): DataResult<ContactDomain> {
        return when (val result = client.saveNewContact(data = data.toDto(), token = token)) {
            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> {

                database.contactDao().saveContact(result.data.toEntity())

                DataResult.Success(data = result.data.toDomain())
            }
        }
    }

    override suspend fun getAllContacts(token: String): DataResult<List<ContactDomain>> {
        return try {
            // First try to get from network
            when (val result = client.getAllContacts(token = token)) {
                is NetworkResult.Error -> {
                    println("$TAG: Network error, falling back to local cache")

                    val localContacts = database.contactDao().getAllContacts().map { it.toDomain() }
                    if (localContacts.isNotEmpty()) {
                        DataResult.Success(data = localContacts)
                    } else {
                        DataResult.Error(result.message)
                    }
                }

                is NetworkResult.Success -> {

                    // Sync received data with local database
//                    syncContactsWithLocalDb(result.data.map { it.toEntity() })
                    DataResult.Success(data = result.data.map { it.toDomain() })
                }
            }
        } catch (e: Exception) {
            println("$TAG: Exception fetching contacts: ${e.message}")
            // In case of any exception, try to return cached data
            val localContacts = database.contactDao().getAllContacts().map { it.toDomain() }
            if (localContacts.isNotEmpty()) {
                DataResult.Success(data = localContacts)
            } else {
                DataResult.Error("Failed to fetch contacts: ${e.message}")
            }
        }
    }

    override suspend fun updateContacts(
        data: ContactsRequestDomain,
        id: String
    ): DataResult<Boolean> {
        return try {
            // Update on server
            val remoteResult = client.updateContact(data = data.toDto(), id = id)

            when (remoteResult) {
                is NetworkResult.Error -> DataResult.Error(remoteResult.message)
                is NetworkResult.Success -> {
                    // On success, update local database
                    val contactId =
                        id.toIntOrNull()
                    val existingContact = database.contactDao().getContactById(contactId as Int)

                    if (existingContact != null) {

                        val updatedEntity = remoteResult.data.copy(id = contactId)
                        database.contactDao().updateContact(updatedEntity.toEntity())
                    } else {

                        database.contactDao()
                            .saveContact(remoteResult.data.toEntity().copy(id = contactId))
                    }

                    DataResult.Success(true)
                }
            }
        } catch (e: Exception) {
            println("$TAG: Exception updating contact: ${e.message}")
            DataResult.Error("Failed to update contact: ${e.message}")
        }
    }

    override suspend fun deleteContacts(id: String): DataResult<Boolean> {
        return try {
            // Delete from server
            val remoteResult = client.deleteContact(id = id)

            when (remoteResult) {
                is NetworkResult.Error -> DataResult.Error(remoteResult.message)
                is NetworkResult.Success -> {
                    // On success, delete from local database
                    val contactId = id
                    database.contactDao().deleteContactById(contactId.toInt())
                    DataResult.Success(true)
                }
            }
        } catch (e: Exception) {
            println("$TAG: Exception deleting contact: ${e.message}")
            DataResult.Error("Failed to delete contact: ${e.message}")
        }
    }

    override suspend fun refreshToken(data: RefreshTokenRequestDomain): DataResult<RefreshTokenResponseDomain> {
        val result = client.refreshToken(data = data.toDto())

        println("$TAG: refresh token result: $result and request data: $data")

        return when (result) {
            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    suspend fun getContactById(id: Int): DataResult<ContactDomain> {
        val contact = database.contactDao().getContactById(id)
        return if (contact != null) {
            DataResult.Success(contact.toDomain())
        } else {
            DataResult.Error("Contact not found")
        }
    }

    fun getContactByIdFlow(id: Int): Flow<DataResult<ContactDomain>> {
        return database.contactDao().getContactByIdFlow(id).map { entity ->
            if (entity != null) {
                DataResult.Success(entity.toDomain())
            } else {
                DataResult.Error("Contact not found")
            }
        }
    }


//    private suspend fun syncContactsWithLocalDb(contacts: List<ContactEntity>) {
//        val pendingContacts = contactDao.getPendingSyncContacts()
//
//        pendingContacts.forEach { pendingContact ->
//
//            val pendingContactToBeSynced = contacts.find { it.id == pendingContact.id }
//
//            if (pendingContactToBeSynced != null) {
//                contactDao.updateContact(pendingContactToBeSynced)
//            }else{
//                return@forEach
//            }
//        }
//    }

}
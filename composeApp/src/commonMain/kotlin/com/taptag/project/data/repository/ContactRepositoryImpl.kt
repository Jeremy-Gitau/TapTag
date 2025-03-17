package com.taptag.project.data.repository

import com.taptag.project.data.mappers.toDomain
import com.taptag.project.data.mappers.toDto
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.models.ContactsResponseDomain
import com.taptag.project.domain.repository.ContactsRepository
import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.dtos.ContactsRequestData
import com.taptag.project.sources.remote.helpers.NetworkResult

class ContactRepositoryImpl(
    private val client: NfcServerClient
) : ContactsRepository {

    override suspend fun saveNewContact(data: ContactsRequestDomain): DataResult<ContactDomain> {
        return when (val result = client.saveNewContact(data = data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun getAllContacts(): DataResult<List<ContactDomain>> {
        return when (val result = client.getAllContacts()) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

}
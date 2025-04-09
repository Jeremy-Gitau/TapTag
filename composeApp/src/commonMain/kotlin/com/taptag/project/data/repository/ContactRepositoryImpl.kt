package com.taptag.project.data.repository

import com.taptag.project.data.mappers.toDomain
import com.taptag.project.data.mappers.toDto
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.ContactDomain
import com.taptag.project.domain.models.ContactsRequestDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.domain.repository.ContactsRepository
import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.helpers.NetworkResult

class ContactRepositoryImpl(
    private val client: NfcServerClient
) : ContactsRepository {

    override suspend fun saveNewContact(
        data: ContactsRequestDomain,
        token: String
    ): DataResult<ContactDomain> {
        return when (val result = client.saveNewContact(data = data.toDto(), token = token)) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun getAllContacts(token: String): DataResult<List<ContactDomain>> {
        return when (val result = client.getAllContacts(token = token)) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.map { it.toDomain() })
        }
    }

    override suspend fun updateContacts(
        data: ContactsRequestDomain,
        id: String
    ): DataResult<Boolean> {

        client.updateContact(data = data.toDto(), id = id)

        return DataResult.Success(true)
    }


    override suspend fun deleteContacts(id: String): DataResult<Boolean> {

        client.deleteContact(id = id)

        return DataResult.Success(true)
    }

    override suspend fun refreshToken(data: RefreshTokenRequestDomain): DataResult<RefreshTokenResponseDomain> {

        return when (val result = client.refreshToken(data = data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

}
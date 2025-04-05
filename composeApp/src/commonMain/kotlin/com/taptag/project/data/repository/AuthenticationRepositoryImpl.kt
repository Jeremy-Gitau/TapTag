package com.taptag.project.data.repository

import com.taptag.project.data.mappers.toDomain
import com.taptag.project.data.mappers.toDto
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.helpers.NetworkResult

class AuthenticationRepositoryImpl(
    private val client: NfcServerClient
) : AuthenticationRepository {

    override suspend fun registerUser(data: AuthRequestDomain): DataResult<AuthResponseDomain> {
        val result = client.registerUser(data = data.toDto())

        println("registered data: $data")
        println("repository register output: $result")

        return when (result) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun loginUser(data: AuthRequestDomain): DataResult<AuthResponseDomain> {
        return when (val result = client.loginUser(data = data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())

        }
    }
}
package com.taptag.project.data.repository

import com.taptag.project.data.database.TapTagDatabase
import com.taptag.project.data.mappers.toDomain
import com.taptag.project.data.mappers.toDto
import com.taptag.project.data.mappers.toEntity
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.sources.remote.client.NfcServerClient
import com.taptag.project.sources.remote.helpers.NetworkResult

class AuthenticationRepositoryImpl(
    private val client: NfcServerClient,
    private val database: TapTagDatabase
) : AuthenticationRepository {

    override suspend fun registerUser(data: AuthRequestDomain): DataResult<AuthResponseDomain> {
        val result = client.registerUser(data = data.toDto())

        println("registered data: $data")
        println("repository register output: $result")

        return when (result) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> {

                database.userDao().saveUser(result.data.toEntity())
                DataResult.Success(data = result.data.toDomain())
            }
        }
    }

    override suspend fun loginUser(data: AuthRequestDomain): DataResult<AuthResponseDomain> {
        return when (val result = client.loginUser(data = data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> {

                database.userDao().saveUser(result.data.toEntity())
                DataResult.Success(data = result.data.toDomain())
            }

        }
    }

    override suspend fun getCachedUser(userId: String): DataResult<AuthResponseDomain?> {
        val cachedUser = database.userDao().getUserById(userId)
        return if (cachedUser != null) {
            DataResult.Success(cachedUser.toDomain())
        } else {
            DataResult.Success(null)
        }
    }

    override suspend fun refreshToken(data: RefreshTokenRequestDomain): DataResult<RefreshTokenResponseDomain> {

        return when (val result = client.refreshToken(data = data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun clearUserData(id: String) {
        database.userDao().clearUserById(id)
    }

    override suspend fun logoutUser(token: String): DataResult<Boolean> {

        client.logout(token)

        return DataResult.Success(true)
    }
}
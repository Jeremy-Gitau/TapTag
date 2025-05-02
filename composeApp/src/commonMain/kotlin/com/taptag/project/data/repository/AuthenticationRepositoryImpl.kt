package com.taptag.project.data.repository

import com.taptag.project.data.database.TapTagDatabase
import com.taptag.project.data.mappers.toDomain
import com.taptag.project.data.mappers.toDto
import com.taptag.project.data.mappers.toEntity
import com.taptag.project.data.mappers.toProfileDomain
import com.taptag.project.data.mappers.toProfileEntity
import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.ChangePasswordRequestDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.domain.models.UserProfileDomain
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
                database.userProfileDao().saveUserProfile(result.data.toProfileEntity())
                DataResult.Success(data = result.data.toDomain())
            }
        }
    }

    override suspend fun loginUser(data: AuthRequestDomain): DataResult<AuthResponseDomain> {
        val result = client.loginUser(data = data.toDto())
        println("AuthRepo: login user result: $result")
        return when (result) {

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

    override suspend fun getCachedUserProfile(userId: String): DataResult<UserProfileDomain?> {
        val cachedUser = database.userProfileDao().getUserProfileById(userId)
        return (if (cachedUser != null) {
            DataResult.Success(cachedUser.toProfileDomain())
        } else {
            DataResult.Success(null)
        })
    }

    override suspend fun refreshToken(data: RefreshTokenRequestDomain): DataResult<RefreshTokenResponseDomain> {

        return when (val result = client.refreshToken(data = data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)
            is NetworkResult.Success -> DataResult.Success(data = result.data.toDomain())
        }
    }

    override suspend fun changePassword(data: ChangePasswordRequestDomain): DataResult<Boolean> {

        return when (val result = client.changePassword(data.toDto())) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> DataResult.Success(result.data)
        }
    }

    override suspend fun forgotPassword(email: String): DataResult<Boolean> {

        return when (val result = client.forgotPassword(email)) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> DataResult.Success(result.data)
        }
    }

    override suspend fun checkExistingUser(email: String): DataResult<Boolean> {
        return when (val result = client.checkExistingUser(email)) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> DataResult.Success(result.data)
        }
    }

    override suspend fun saveUserProfile(
        token: String,
        data: UserProfileDomain
    ): DataResult<UserProfileDomain> {
        return when (val result = client.saveUserProfile(
            token = token,
            data = data.toDto()
        )) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> {

                database.userProfileDao().saveUserProfile(result.data.toProfileEntity())
                DataResult.Success(result.data.toDomain())
            }
        }
    }

    override suspend fun fetchUserProfile(token: String): DataResult<UserProfileDomain> {
        return when (val result = client.fetchUserProfile(token = token)) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> DataResult.Success(result.data.toDomain())
        }
    }

    override suspend fun updateUserProfile(
        id: String,
        data: UserProfileDomain
    ): DataResult<UserProfileDomain> {
        return when (
            val result = client.updateUserProfile(
                id = id,
                data = data.toDto()
            )
        ) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> DataResult.Success(result.data.toDomain())
        }
    }

    override suspend fun deleteUserProfile(id: String): DataResult<Boolean> {
        return when (val result = client.deleteUserProfile(id = id)) {

            is NetworkResult.Error -> DataResult.Error(result.message)

            is NetworkResult.Success -> DataResult.Success(result.data)
        }
    }

    override suspend fun clearUserData(id: String) {
        database.userDao().clearUserById(id)
    }

    override suspend fun clearUserProfileData(id: String) {
        database.userProfileDao().deleteUserProfileById(id)
    }

    override suspend fun logoutUser(token: String): DataResult<Boolean> {

        client.logout(token)

        return DataResult.Success(true)
    }
}
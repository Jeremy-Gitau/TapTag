package com.taptag.project.domain.repository

import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain
import com.taptag.project.domain.models.ChangePasswordRequestDomain
import com.taptag.project.domain.models.RefreshTokenRequestDomain
import com.taptag.project.domain.models.RefreshTokenResponseDomain
import com.taptag.project.domain.models.UserProfileDomain
import com.taptag.project.sources.remote.dtos.ChangePasswordRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenRequestData
import com.taptag.project.sources.remote.dtos.RefreshTokenResponseData
import com.taptag.project.sources.remote.dtos.UserProfileDto
import com.taptag.project.sources.remote.helpers.NetworkResult

interface AuthenticationRepository {

    suspend fun registerUser(data: AuthRequestDomain): DataResult<AuthResponseDomain>

    suspend fun loginUser(data: AuthRequestDomain): DataResult<AuthResponseDomain>

    suspend fun getCachedUser(userId: String): DataResult<AuthResponseDomain?>

    suspend fun refreshToken(data: RefreshTokenRequestDomain): DataResult<RefreshTokenResponseDomain>

    suspend fun changePassword(data: ChangePasswordRequestDomain): DataResult<Boolean>

    suspend fun forgotPassword(email: String): DataResult<Boolean>

    suspend fun checkExistingUser(email: String): DataResult<Boolean>

    suspend fun saveUserProfile(
        token: String,
        data: UserProfileDomain
    ): DataResult<UserProfileDomain>

    suspend fun fetchUserProfile(token: String): DataResult<UserProfileDomain>

    suspend fun getCachedUserProfile(userId: String): DataResult<UserProfileDomain?>

    suspend fun updateUserProfile(
        id: String,
        data: UserProfileDomain
    ): DataResult<UserProfileDomain>

    suspend fun deleteUserProfile(id: String): DataResult<Boolean>

    suspend fun clearUserData(id: String)

    suspend fun clearUserProfileData(id: String)

    suspend fun logoutUser(token: String): DataResult<Boolean>

}
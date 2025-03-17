package com.taptag.project.domain.repository

import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.UserRequestDomain
import com.taptag.project.domain.models.UserResponseDomain

interface AuthenticationRepository {

    suspend fun registerUser(data: UserRequestDomain): DataResult<UserResponseDomain>
    suspend fun loginUser(data: UserRequestDomain): DataResult<UserResponseDomain>

}
package com.taptag.project.domain.repository

import com.taptag.project.domain.helpers.DataResult
import com.taptag.project.domain.models.AuthRequestDomain
import com.taptag.project.domain.models.AuthResponseDomain

interface AuthenticationRepository {

    suspend fun registerUser(data: AuthRequestDomain): DataResult<AuthResponseDomain>
    suspend fun loginUser(data: AuthRequestDomain): DataResult<AuthResponseDomain>

}
package com.taptag.project.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val isDarkModeEnabled: Flow<Boolean>
    suspend fun toggleDarkMode()

    suspend fun saveAccessToken(token: String): Boolean
    suspend fun saveRefreshToken(token: String): Boolean
    suspend fun readAccessToken(): Flow<String>
    suspend fun readRefreshToken(): Flow<String>
    suspend fun deleteAccessToken()
    suspend fun deleteRefreshToken()

    suspend fun clearAllPreferences()

}
package com.taptag.project.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val isDarkModeEnabled: Flow<Boolean>
    suspend fun toggleDarkMode()

    suspend fun saveAccessToken(token: String): Boolean
    suspend fun readAccessToken(): Flow<String>

    suspend fun clearAllPreferences()

}
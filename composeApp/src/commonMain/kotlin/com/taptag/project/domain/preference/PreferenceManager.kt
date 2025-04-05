package com.taptag.project.domain.preference

import kotlinx.coroutines.flow.Flow

interface PreferenceManager {

    val isDarkModeEnabled: Flow<Boolean>
    suspend fun toggleDarkMode()

    suspend fun saveAccessToken(token: String): Boolean
    suspend fun readAccessToken(): Flow<String>

    suspend fun clearAllPreferences()

}
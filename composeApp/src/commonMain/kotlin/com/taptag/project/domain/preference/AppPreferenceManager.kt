package com.taptag.project.domain.preference

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface AppPreferenceManager {

    // system dark mode operations
    val isDarkModeEnabled: Flow<Boolean>
    suspend fun toggleDarkMode()

    // access token operations
    suspend fun saveAccessToken(token: String): Boolean
    suspend fun saveRefreshToken(token: String): Boolean
    suspend fun readAccessToken(): Flow<String>
    suspend fun readRefreshToken(): Flow<String>
    suspend fun deleteAccessToken()
    suspend fun deleteRefreshToken()

    suspend fun clearAllPreferences()

}
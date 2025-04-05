package com.taptag.project.data.repository

import com.taptag.project.domain.preference.PreferenceManager
import com.taptag.project.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow

class PreferenceRepositoryImpl(
    private val preferenceManager: PreferenceManager
): PreferenceRepository {

    override val isDarkModeEnabled: Flow<Boolean>
        get() = preferenceManager.isDarkModeEnabled

    override suspend fun toggleDarkMode() {
        preferenceManager.toggleDarkMode()
    }

    override suspend fun saveAccessToken(token: String): Boolean {
        return preferenceManager.saveAccessToken(token = token)
    }

    override suspend fun readAccessToken(): Flow<String> {
        return preferenceManager.readAccessToken()
    }

    override suspend fun clearAllPreferences() {
        preferenceManager.clearAllPreferences()
    }
}
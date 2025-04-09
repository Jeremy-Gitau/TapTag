package com.taptag.project.data.repository

import com.taptag.project.domain.preference.AppPreferenceManager
import com.taptag.project.domain.repository.PreferenceRepository
import kotlinx.coroutines.flow.Flow

class PreferenceRepositoryImpl(
    private val preferenceManager: AppPreferenceManager
): PreferenceRepository {

    override val isDarkModeEnabled: Flow<Boolean>
        get() = preferenceManager.isDarkModeEnabled

    override suspend fun toggleDarkMode() {
        preferenceManager.toggleDarkMode()
    }

    override suspend fun saveAccessToken(token: String): Boolean {
        return preferenceManager.saveAccessToken(token = token)
    }

    override suspend fun saveRefreshToken(token: String): Boolean {
        return preferenceManager.saveRefreshToken(token = token)
    }

    override suspend fun readAccessToken(): Flow<String> {
        return preferenceManager.readAccessToken()
    }

    override suspend fun readRefreshToken(): Flow<String> {
        return preferenceManager.readRefreshToken()
    }

    override suspend fun deleteAccessToken(){
        preferenceManager.deleteAccessToken()
    }

    override suspend fun deleteRefreshToken(){
        preferenceManager.deleteRefreshToken()
    }

    override suspend fun clearAllPreferences() {
        preferenceManager.clearAllPreferences()
    }
}
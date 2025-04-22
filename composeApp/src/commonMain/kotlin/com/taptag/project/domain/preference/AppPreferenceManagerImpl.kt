package com.taptag.project.domain.preference

import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.taptag.project.sources.local.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppPreferenceManagerImpl(
    private val source: PreferenceSource
) : AppPreferenceManager {

    companion object {
        val darkMode = booleanPreferencesKey(name = "dark_mode")
        val accessToken = stringPreferencesKey(name = "access_token")
        val refreshToken = stringPreferencesKey(name = "refresh_token")
        val USERID = stringPreferencesKey(name = "user_id")
    }

    override val isDarkModeEnabled: Flow<Boolean>
        get() = source.get(key = darkMode, defaultValue = false)

    override suspend fun toggleDarkMode() {
        val isDarkEnabled = isDarkModeEnabled.first()

        source.update(darkMode, value = isDarkEnabled.not())
    }

    override suspend fun saveAccessToken(token: String): Boolean =
        try {
            source.save(accessToken, token)
            true
        } catch (e: Exception) {
            println("saveAccessToken() Error: ${e.message}")
            false
        }

    override suspend fun saveRefreshToken(token: String): Boolean =
        try {
            source.save(refreshToken, token)
            true
        } catch (e: Exception) {
            println("saveRefreshToken() Error: ${e.message}")
            false
        }

    override suspend fun saveUserId(userId: String): Boolean =
        try {
            source.save(USERID, userId)
            true
        } catch (e: Exception) {
            println("saveUserId error: ${e.message}")
            false
        }


    override suspend fun readAccessToken(): Flow<String> =
        source.get(accessToken, defaultValue = "")

    override suspend fun readRefreshToken(): Flow<String> =
        source.get(refreshToken, defaultValue = "")

    override suspend fun readUserId(): Flow<String> =
        source.get(USERID, defaultValue = "")


    override suspend fun deleteAccessToken() {
        try {
            source.delete(accessToken)
        } catch (e: IOException) {
            println(e.message)
        }
    }

    override suspend fun deleteRefreshToken() {
        try {
            source.delete(refreshToken)
        } catch (e: IOException) {
            println(e.message)
        }
    }

    override suspend fun deleteUserId() {
        source.delete(USERID)
    }

    override suspend fun clearAllPreferences() {
        try {
            source.clear()
        } catch (e: IOException) {
            println(e.message)
        }
    }
}
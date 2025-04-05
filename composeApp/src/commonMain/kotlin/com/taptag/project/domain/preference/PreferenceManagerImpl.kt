package com.taptag.project.domain.preference

import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.taptag.project.sources.local.preference.PreferenceSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PreferenceManagerImpl(
    private val source: PreferenceSource
) : PreferenceManager {

    companion object {
        val darkMode = booleanPreferencesKey(name = "dark_mode")
        val accessToken = stringPreferencesKey(name = "access_token")
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

    override suspend fun readAccessToken(): Flow<String> =
        source.get(accessToken, defaultValue = "")

    override suspend fun clearAllPreferences() {
        try {
            source.clear()
        } catch (e: IOException) {
            println(e.message)
        }
    }
}
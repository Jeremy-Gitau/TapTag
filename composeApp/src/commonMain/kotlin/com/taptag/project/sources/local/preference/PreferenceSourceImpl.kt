package com.taptag.project.sources.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map

class PreferenceSourceImpl(
    private val dataStore: DataStore<Preferences>
): PreferenceSource {

    override fun <T> getNullable(key: Preferences.Key<T>): Flow<T?> {
        return dataStore.data.map { it[key] }
    }

    override fun <T> get(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data
            .catch { emptyFlow<Boolean>() }
            .map { it[key] ?: defaultValue }
    }

    override suspend fun <T> save(key: Preferences.Key<T>, value: T) {

        dataStore.edit { it[key] = value }

    }

    override suspend fun <T> update(key: Preferences.Key<T>, value: T) {

        dataStore.edit { it[key] = value }

    }

    override suspend fun <T> delete(key: Preferences.Key<T>) {

        dataStore.edit { it.remove(key) }

    }

    override suspend fun clear() {

        dataStore.edit { it.clear() }

    }
}
package com.taptag.project.sources.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.atomicfu.locks.SynchronizedObject
import okio.Path.Companion.toPath

internal const val datastoreFileName = "settings.preferences_pb"

object AppPreferences {

    private lateinit var dataStore: DataStore<Preferences>
    private val lock = SynchronizedObject()

    fun getDataStore(producePath: () -> String): DataStore<Preferences> {

        return synchronized(lock) {

            if (::dataStore.isInitialized) {

                dataStore

            } else {

                PreferenceDataStoreFactory.createWithPath(
                    produceFile = { producePath().toPath() }).also { dataStore = it }

            }
        }
    }
}
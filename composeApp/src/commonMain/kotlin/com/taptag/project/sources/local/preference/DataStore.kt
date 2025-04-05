package com.taptag.project.sources.local.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(context: Any? = null): DataStore<Preferences>
package com.taptag.project.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.taptag.project.sources.local.preference.createDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single<DataStore<Preferences>> { createDataStore(androidContext()) }

}
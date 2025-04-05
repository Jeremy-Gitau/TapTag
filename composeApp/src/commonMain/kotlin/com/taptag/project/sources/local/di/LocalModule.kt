package com.taptag.project.sources.local.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.taptag.project.sources.local.preference.PreferenceSource
import com.taptag.project.sources.local.preference.PreferenceSourceImpl
import com.taptag.project.sources.local.preference.createDataStore
import org.koin.dsl.module

val localModule = module {

//    single<DataStore<Preferences>> { createDataStore() }

    single<PreferenceSource> { PreferenceSourceImpl(get()) }
}
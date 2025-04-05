package com.taptag.project.sources.local.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

actual fun createDataStore(context: Any?): DataStore<Preferences> {

    require(
        value = context is Context,
        lazyMessage = {"Context Object is required."}
    )

    return AppPreferences.getDataStore(
        producePath = {
            context.filesDir
                .resolve(datastoreFileName)
                .absolutePath
        }
    )

}
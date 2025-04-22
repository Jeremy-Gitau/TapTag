package com.taptag.project.app

import com.taptag.project.data.di.dataModule
import com.taptag.project.data.di.platformModule
import com.taptag.project.sources.local.di.localModule
import com.taptag.project.sources.remote.di.clientsModule
import com.taptag.project.ui.di.uiModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: (KoinApplication.() -> Unit)? = null) {

    startKoin {
        config?.invoke(this)
        modules(dataModule, platformModule(), clientsModule, uiModule, localModule)
    }
}
package com.taptag.project.app

import com.taptag.project.data.di.dataModule
import com.taptag.project.data.di.platformModule
import com.taptag.project.sources.remote.di.clientsModule
import com.taptag.project.ui.di.uiModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {

    startKoin {
        appDeclaration()
        modules(dataModule, platformModule(), clientsModule, uiModule)
    }
}
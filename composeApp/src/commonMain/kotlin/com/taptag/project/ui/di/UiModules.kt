package com.taptag.project.ui.di

import com.taptag.project.ui.screens.NFCScreen.NFCScreenModel
import com.taptag.project.ui.screens.authentication.UserScreenModel
import com.taptag.project.ui.screens.contact.ContactScreenModel
import com.taptag.project.ui.screens.settings.SettingsScreenModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val uiModule = module {

//    single { NFCScreenModel(useCase = get()) }
    singleOf(::NFCScreenModel)
    single { UserScreenModel(get(), get()) }
    single { ContactScreenModel(get(), get()) }
    single { SettingsScreenModel(get()) }

}
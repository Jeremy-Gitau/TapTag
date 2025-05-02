package com.taptag.project.data.di

import com.taptag.project.data.database.getRoomDatabase
import com.taptag.project.data.repository.AuthenticationRepositoryImpl
import com.taptag.project.data.repository.ContactRepositoryImpl
import com.taptag.project.domain.preference.AppPreferenceManagerImpl
import com.taptag.project.data.repository.PaymentRepositoryImpl
import com.taptag.project.data.repository.PreferenceRepositoryImpl
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.domain.repository.ContactsRepository
import com.taptag.project.domain.preference.AppPreferenceManager
import com.taptag.project.domain.repository.PaymentsRepository
import com.taptag.project.domain.repository.PreferenceRepository
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val dataModule = module {

    single<AuthenticationRepository> { AuthenticationRepositoryImpl(get(), get()) }
    single<ContactsRepository> { ContactRepositoryImpl(get(), get()) }
    single<PaymentsRepository> { PaymentRepositoryImpl(get()) }
    single<AppPreferenceManager> { AppPreferenceManagerImpl(get()) }
    single<PreferenceRepository> { PreferenceRepositoryImpl(get()) }

    single { getRoomDatabase(get()) }

}
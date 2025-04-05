package com.taptag.project.data.di

import com.taptag.project.data.repository.AuthenticationRepositoryImpl
import com.taptag.project.data.repository.ContactRepositoryImpl
import com.taptag.project.domain.preference.PreferenceManagerImpl
import com.taptag.project.data.repository.PaymentRepositoryImpl
import com.taptag.project.data.repository.PreferenceRepositoryImpl
import com.taptag.project.domain.repository.AuthenticationRepository
import com.taptag.project.domain.repository.ContactsRepository
import com.taptag.project.domain.preference.PreferenceManager
import com.taptag.project.domain.repository.PaymentsRepository
import com.taptag.project.domain.repository.PreferenceRepository
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val dataModule = module {

    single<AuthenticationRepository> { AuthenticationRepositoryImpl(get()) }
    single<ContactsRepository> { ContactRepositoryImpl(get()) }
    single<PaymentsRepository> { PaymentRepositoryImpl(get()) }
    single<PreferenceManager> { PreferenceManagerImpl(get()) }
    single<PreferenceRepository> { PreferenceRepositoryImpl(get()) }

}
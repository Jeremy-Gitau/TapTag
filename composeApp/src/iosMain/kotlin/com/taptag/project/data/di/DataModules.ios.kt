package com.taptag.project.data.di

import com.taptag.project.domain.repository.NFCRepository
import com.taptag.project.domain.repository.NFCRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<NFCRepository> { NFCRepositoryImpl() }
}
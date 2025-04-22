package com.taptag.project

import android.app.Application
import com.taptag.project.app.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class NFCApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@NFCApplication)
        }
    }
}
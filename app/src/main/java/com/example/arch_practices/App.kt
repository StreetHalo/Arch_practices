package com.example.arch_practices

import android.app.Application
import com.example.arch_practices.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppArch: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppArch)
            modules(dataModule)
        }
    }
}
package com.example.eventorias

import android.app.Application
import com.example.eventorias.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class EventoriaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@EventoriaApplication)
            modules(appModule)
        }
    }
}
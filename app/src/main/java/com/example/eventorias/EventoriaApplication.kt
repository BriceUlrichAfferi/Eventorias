package com.example.eventorias

import android.app.Application
import com.example.eventorias.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EventoriaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize ThreeTenABP for Java 8's time API on Android
        //AndroidThreeTen.init(this)

        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@EventoriaApplication)
            modules(appModule)
        }
    }
}
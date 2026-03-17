package com.kobby.hymnal

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.kobby.hymnal.di.androidModule
import com.kobby.hymnal.di.crashlyticsModule
import com.kobby.hymnal.di.databaseModule
import com.kobby.hymnal.di.settingsModule
import com.kobby.hymnal.di.subscriptionModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HymnalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@HymnalApplication)
            modules(
                databaseModule,
                settingsModule,
                androidModule,
                crashlyticsModule,
                subscriptionModule
            )
        }

        // Initialize Firebase
        Firebase.initialize(this)
    }
}

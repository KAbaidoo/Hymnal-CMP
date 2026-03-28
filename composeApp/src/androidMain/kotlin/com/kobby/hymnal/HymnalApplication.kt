package com.kobby.hymnal

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.kobby.hymnal.core.util.ActivityProvider
import com.kobby.hymnal.di.androidModule
import com.kobby.hymnal.di.crashlyticsModule
import com.kobby.hymnal.di.databaseModule
import com.kobby.hymnal.di.settingsModule
import com.kobby.hymnal.di.subscriptionModule
import com.kobby.hymnal.di.updateModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class HymnalApplication : Application() {
    private val activityProvider = ActivityProvider()

    override fun onCreate() {
        super.onCreate()

        // Register ActivityProvider for lifecycle tracking
        registerActivityLifecycleCallbacks(activityProvider)

        // Initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@HymnalApplication)
            modules(
                databaseModule,
                settingsModule,
                androidModule,
                crashlyticsModule,
                subscriptionModule,
                updateModule,
                module { single { activityProvider } } // Register ActivityProvider in Koin
            )
        }

        // Initialize Firebase
        Firebase.initialize(this)
    }
}

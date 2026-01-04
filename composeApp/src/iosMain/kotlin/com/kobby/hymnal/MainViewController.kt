package com.kobby.hymnal

import androidx.compose.ui.window.ComposeUIViewController
import com.kobby.hymnal.core.iap.SubscriptionManager
import com.kobby.hymnal.di.crashlyticsModule
import com.kobby.hymnal.di.databaseModule
import com.kobby.hymnal.di.iosModule
import com.kobby.hymnal.di.settingsModule
import com.kobby.hymnal.di.subscriptionModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

fun MainViewController() = ComposeUIViewController { 
    // Initialize Koin for iOS
    try {
        startKoin {
            logger(PrintLogger(Level.DEBUG))
            modules(databaseModule, settingsModule, iosModule, crashlyticsModule, subscriptionModule)
        }
    } catch (e: Exception) {
        // Koin already started
    }
    
    // Initialize subscription manager for trial tracking and entitlement state
    object : KoinComponent {
        init {
            val subscriptionManager: SubscriptionManager by inject()
            subscriptionManager.initialize()
        }
    }

    HymnalApp()
}


package com.kobby.hymnal

import androidx.compose.ui.window.ComposeUIViewController
import com.kobby.hymnal.di.crashlyticsModule
import com.kobby.hymnal.di.databaseModule
import com.kobby.hymnal.di.iosModule
import com.kobby.hymnal.di.settingsModule
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger

fun MainViewController() = ComposeUIViewController { 
    // Initialize Koin for iOS
    try {
        startKoin {
            logger(PrintLogger(Level.DEBUG))
            modules(databaseModule, settingsModule, iosModule, crashlyticsModule)
        }
    } catch (e: Exception) {
        // Koin already started
    }
    
    HymnalApp() 
}
package com.kobby.hymnal.core.database

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseInitializer : KoinComponent {
    private val driverFactory: DriverFactory by inject()
    private val hymnRepository: HymnRepository by inject()
    
    actual suspend fun initialize() {
        // Initialization is now handled by Koin
        // The database and repository are created when first accessed
    }
    
    actual fun getRepository(): HymnRepository {
        return hymnRepository
    }
}
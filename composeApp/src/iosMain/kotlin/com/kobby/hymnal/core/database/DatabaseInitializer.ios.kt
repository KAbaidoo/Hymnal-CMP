package com.kobby.hymnal.core.database

actual class DatabaseInitializer {
    private val driverFactory = DriverFactory()
    
    actual suspend fun initialize() {
        DatabaseManager.initialize(driverFactory)
    }
    
    actual fun getRepository(): HymnRepository {
        return DatabaseManager.getRepository()
    }
}
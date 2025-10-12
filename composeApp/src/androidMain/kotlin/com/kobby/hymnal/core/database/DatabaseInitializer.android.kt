package com.kobby.hymnal.core.database

import android.content.Context

actual class DatabaseInitializer(private val context: Context) {
    private val driverFactory = DriverFactory(context)
    
    actual suspend fun initialize() {
        DatabaseManager.initialize(driverFactory)
    }
    
    actual fun getRepository(): HymnRepository {
        return DatabaseManager.getRepository()
    }
}
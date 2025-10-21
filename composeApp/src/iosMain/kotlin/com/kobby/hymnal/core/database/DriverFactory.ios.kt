package com.kobby.hymnal.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory {
    
    actual suspend fun createDriver(): SqlDriver {
        val databaseHelper = DatabaseHelper()
        
        // Initialize the database (copy from Compose resources if needed)
        databaseHelper.initializeDatabase()
        
        // Create driver with schema creation disabled since database is pre-packaged
        return NativeSqliteDriver(
            schema = HymnDatabase.Schema,
            name = DATABASE_NAME,
            onConfiguration = { config ->
                config.copy(create = { /* Do nothing - database is pre-packaged */ })
            }
        )
    }
}
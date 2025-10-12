package com.kobby.hymnal.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory {
    private val databaseHelper = DatabaseHelper()
    
    actual suspend fun createDriver(): SqlDriver {
        // Initialize the database (copy from bundle if needed)
        val databasePath = databaseHelper.initializeDatabase()
        
        // Create driver using the existing database
        return NativeSqliteDriver(
            schema = HymnDatabase.Schema,
            name = databasePath
        )
    }
}
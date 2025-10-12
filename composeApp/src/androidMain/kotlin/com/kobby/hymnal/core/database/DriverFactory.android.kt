package com.kobby.hymnal.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory(private val context: Context) {
    
    actual suspend fun createDriver(): SqlDriver {
        // Use a different database name to avoid conflicts with any existing database
        val databaseName = "hymnal_app.db"
        
        // Clear any existing database to start fresh
        context.deleteDatabase(databaseName)
        context.deleteDatabase("hymns.db") // Also delete the old one
        
        // Create driver and let SQLDelight handle schema creation
        return AndroidSqliteDriver(
            schema = HymnDatabase.Schema,
            context = context,
            name = databaseName
        )
    }
}
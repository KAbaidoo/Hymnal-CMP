package com.kobby.hymnal.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory(private val context: Context) {
    private val databaseHelper = DatabaseHelper(context)
    
    actual suspend fun createDriver(): SqlDriver {
        // Initialize the database (copy from assets if needed)
        val databasePath = databaseHelper.initializeDatabase()
        
        // Create driver using the existing database
        return AndroidSqliteDriver(
            schema = HymnDatabase.Schema,
            context = context,
            name = "hymns.db"
        )
    }
}
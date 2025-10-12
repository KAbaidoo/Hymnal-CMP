package com.kobby.hymnal.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory(private val context: Context) {
    
    actual suspend fun createDriver(): SqlDriver {
        // Create driver and let SQLDelight handle schema creation
        return AndroidSqliteDriver(
            schema = HymnDatabase.Schema,
            context = context,
            name = "hymns.db"
        )
    }
}
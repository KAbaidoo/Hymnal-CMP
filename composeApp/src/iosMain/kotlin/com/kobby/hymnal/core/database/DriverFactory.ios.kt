package com.kobby.hymnal.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory {
    
    actual suspend fun createDriver(): SqlDriver {
        // Create driver and let SQLDelight handle schema creation
        return NativeSqliteDriver(
            schema = HymnDatabase.Schema,
            name = "hymns.db"
        )
    }
}
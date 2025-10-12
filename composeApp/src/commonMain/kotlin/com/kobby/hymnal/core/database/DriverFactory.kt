package com.kobby.hymnal.core.database

import app.cash.sqldelight.db.SqlDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

expect class DriverFactory {
    suspend fun createDriver(): SqlDriver
}

suspend fun createDatabase(driverFactory: DriverFactory): HymnDatabase {
    val driver = driverFactory.createDriver()
    return HymnDatabase(driver)
}
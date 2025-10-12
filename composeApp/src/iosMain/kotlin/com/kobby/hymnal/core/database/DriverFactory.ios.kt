package com.kobby.hymnal.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase

actual class DriverFactory {
    private val databaseHelper = DatabaseHelper()
    
    actual suspend fun createDriver(): SqlDriver {
        // Initialize the database (copy from bundle if needed)
        val databasePath = databaseHelper.initializeDatabase()
        
        // Check if database already exists and has data
        val databaseExists = databaseHelper.isDatabaseInitialized()
        
        // Create a custom schema that doesn't create tables for existing databases
        val customSchema = object : SqlSchema<Int> {
            override val version: Int = HymnDatabase.Schema.version
            
            override fun create(driver: SqlDriver): Int {
                // Only create schema if database doesn't already exist with data
                if (!databaseExists) {
                    HymnDatabase.Schema.create(driver)
                }
                return version
            }
            
            override fun migrate(
                driver: SqlDriver,
                oldVersion: Int,
                newVersion: Int,
                vararg callbacks: SqlSchema.Callback
            ): Int {
                // Handle migrations if needed in the future
                return newVersion
            }
        }
        
        // Create driver using custom schema
        return NativeSqliteDriver(
            schema = customSchema,
            name = databasePath
        )
    }
}
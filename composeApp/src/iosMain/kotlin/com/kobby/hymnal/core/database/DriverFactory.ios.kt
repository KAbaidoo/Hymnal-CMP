package com.kobby.hymnal.core.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import kotlinx.cinterop.*
import platform.Foundation.*

actual class DriverFactory(
    private val databaseHelper: DatabaseHelper
) {
    
    actual suspend fun createDriver(): SqlDriver {
        // Initialize the database (copy from Compose resources if needed)
        databaseHelper.initializeDatabase()
        
        val driver = NativeSqliteDriver(
            schema = HymnDatabase.Schema,
            name = DATABASE_NAME,
            onConfiguration = { config ->
                config.copy(create = { /* Do nothing - database is pre-packaged */ })
            }
        )

        // Test database integrity to detect corruption
        try {
            driver.executeQuery(
                identifier = null,
                sql = "SELECT count(*) FROM sqlite_master;",
                mapper = { app.cash.sqldelight.db.QueryResult.Value(Unit) },
                parameters = 0
            )
            return driver
        } catch (e: Exception) {
            println("Database is corrupt or invalid: ${e.message}. Deleting and recreating...")
            try {
                driver.close()
            } catch (_: Exception) {}
            
            // Delete the corrupted database and its temp files
            deleteDatabaseFiles()
            
            // Re-copy from resources
            databaseHelper.initializeDatabase()
            
            // Return a new driver
            return NativeSqliteDriver(
                schema = HymnDatabase.Schema,
                name = DATABASE_NAME,
                onConfiguration = { config ->
                    config.copy(create = { /* Do nothing - database is pre-packaged */ })
                }
            )
        }
    }

    @OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
    private fun deleteDatabaseFiles() {
        val databasePath = databaseHelper.getDatabasePath()
        val fileManager = NSFileManager.defaultManager
        
        val filesToDelete = listOf(
            databasePath,
            "$databasePath-journal",
            "$databasePath-wal",
            "$databasePath-shm"
        )
        
        for (filePath in filesToDelete) {
            if (fileManager.fileExistsAtPath(filePath)) {
                memScoped {
                    val error = alloc<ObjCObjectVar<NSError?>>()
                    val success = fileManager.removeItemAtPath(filePath, error.ptr)
                    if (!success) {
                        println("Failed to delete SQLite file $filePath: ${error.value?.localizedDescription}")
                    } else {
                        println("Deleted SQLite file: $filePath")
                    }
                }
            }
        }
    }
}
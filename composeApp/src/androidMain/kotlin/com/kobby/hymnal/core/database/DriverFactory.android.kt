package com.kobby.hymnal.core.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kobby.hymnal.composeApp.database.HymnDatabase
import java.io.File

actual class DriverFactory(private val context: Context) {
    
    actual suspend fun createDriver(): SqlDriver {
        val databaseHelper = DatabaseHelper(context)
        
        // Initialize the database (copy from assets if needed)
        val databasePath = databaseHelper.initializeDatabase()
        
        // Extract just the database name from the full path
        val databaseName = File(databasePath).name
        
        // Create custom callback for pre-packaged database
        val callback = object : AndroidSqliteDriver.Callback(HymnDatabase.Schema) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // Do nothing - database is already pre-packaged with all tables and data
                // This prevents "table already exists" error
            }
            
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Optionally set pragmas here if needed
                // db.execSQL("PRAGMA foreign_keys=ON;")
            }
        }
        
        // Create driver using the initialized database with custom callback
        return AndroidSqliteDriver(
            schema = HymnDatabase.Schema,
            context = context,
            name = databaseName,
            callback = callback
        )
    }
}
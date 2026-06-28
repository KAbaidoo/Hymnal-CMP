package com.kobby.hymnal.core.database

import android.content.Context
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

actual class DatabaseHelper(private val context: Context, private val settings: Settings) {
    
    private val databaseName = DATABASE_NAME
    private val prepackagedDatabasePath = "composeResources/hymnal_cmp.composeapp.generated.resources/files/$databaseName"
    
    actual suspend fun initializeDatabase(): String = withContext(Dispatchers.IO) {
        val databaseFile = File(context.getDatabasePath(databaseName).absolutePath)
        val installedDbVersion = settings.getInt("installed_db_version", 1)
        
        if (!isDatabaseInitialized() || installedDbVersion < CURRENT_DATABASE_VERSION) {
            // Create database directory if it doesn't exist
            databaseFile.parentFile?.mkdirs()
            
            // Delete existing file if we are updating
            if (databaseFile.exists()) {
                databaseFile.delete()
            }
            
            // Copy prepackaged database from assets
            copyDatabaseFromAssets(databaseFile)
            
            // Update installed version
            settings.putInt("installed_db_version", CURRENT_DATABASE_VERSION)
        }
        
        databaseFile.absolutePath
    }
    
    actual fun getDatabasePath(): String {
        return context.getDatabasePath(databaseName).absolutePath
    }
    
    actual suspend fun isDatabaseInitialized(): Boolean = withContext(Dispatchers.IO) {
        val databaseFile = File(getDatabasePath())
        databaseFile.exists() && databaseFile.length() > 0
    }
    
    private suspend fun copyDatabaseFromAssets(databaseFile: File) = withContext(Dispatchers.IO) {
        try {
            val inputStream: InputStream = context.assets.open(prepackagedDatabasePath)
            val outputStream = FileOutputStream(databaseFile)
            
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            println("Database copied successfully from assets to ${databaseFile.absolutePath}")
        } catch (e: Exception) {
            throw RuntimeException("Failed to copy database from assets: ${e.message}", e)
        }
    }
    
}
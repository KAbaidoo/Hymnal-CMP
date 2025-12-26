package com.kobby.hymnal.core.database

import android.content.Context
import com.kobby.hymnal.core.performance.PerformanceManager
import com.kobby.hymnal.core.performance.traceSuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

actual class DatabaseHelper(
    private val context: Context,
    private val performanceManager: PerformanceManager? = null
) {
    
    private val databaseName = DATABASE_NAME
    private val prepackagedDatabasePath = "composeResources/hymnal_cmp.composeapp.generated.resources/files/$databaseName"
    
    actual suspend fun initializeDatabase(): String = withContext(Dispatchers.IO) {
        performanceManager?.traceSuspend("db_initialization") { trace ->
            val databaseFile = File(context.getDatabasePath(databaseName).absolutePath)
            
            if (!isDatabaseInitialized()) {
                trace.putAttribute("action", "copy_from_assets")
                // Create database directory if it doesn't exist
                databaseFile.parentFile?.mkdirs()
                
                // Copy prepackaged database from assets
                copyDatabaseFromAssets(databaseFile)
                trace.putMetric("database_size_bytes", databaseFile.length())
            } else {
                trace.putAttribute("action", "already_initialized")
            }
            
            databaseFile.absolutePath
        } ?: run {
            val databaseFile = File(context.getDatabasePath(databaseName).absolutePath)
            
            if (!isDatabaseInitialized()) {
                databaseFile.parentFile?.mkdirs()
                copyDatabaseFromAssets(databaseFile)
            }
            
            databaseFile.absolutePath
        }
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
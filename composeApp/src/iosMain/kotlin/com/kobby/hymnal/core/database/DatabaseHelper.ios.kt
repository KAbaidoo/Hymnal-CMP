package com.kobby.hymnal.core.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*
import platform.darwin.DISPATCH_QUEUE_PRIORITY_DEFAULT
import platform.darwin.dispatch_get_global_queue

actual class DatabaseHelper() {
    
    private val databaseName = "hymns.db"
    
    actual suspend fun initializeDatabase(): String = withContext(Dispatchers.Default) {
        val databasePath = getDatabasePath()
        
        if (!isDatabaseInitialized()) {
            copyDatabaseFromBundle(databasePath)
        }
        
        databasePath
    }
    
    actual fun getDatabasePath(): String {
        val documentsPath = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: throw RuntimeException("Could not access documents directory")
        
        return "$documentsPath/$databaseName"
    }
    
    actual suspend fun isDatabaseInitialized(): Boolean = withContext(Dispatchers.Default) {
        val databasePath = getDatabasePath()
        NSFileManager.defaultManager.fileExistsAtPath(databasePath)
    }
    
    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
    private suspend fun copyDatabaseFromBundle(databasePath: String) = withContext(Dispatchers.Default) {
        try {
            val bundle = NSBundle.mainBundle
            val bundlePath = bundle.pathForResource(databaseName, "db") 
                ?: bundle.pathForResource("composeResources/hymnal_cmp.composeapp.generated.resources/files/$databaseName", null)
                ?: throw RuntimeException("Could not find prepackaged database in bundle")
            
            val fileManager = NSFileManager.defaultManager
            
            // Copy file from bundle to documents directory
            kotlinx.cinterop.memScoped {
                val error = kotlinx.cinterop.alloc<kotlinx.cinterop.ObjCObjectVar<NSError?>>()
                val success = fileManager.copyItemAtPath(bundlePath, databasePath, error.ptr)
                
                if (!success) {
                    throw RuntimeException("Failed to copy database from bundle")
                }
            }
            
            println("Database copied successfully from bundle to $databasePath")
        } catch (e: Exception) {
            throw RuntimeException("Failed to copy database from bundle: ${e.message}", e)
        }
    }
}
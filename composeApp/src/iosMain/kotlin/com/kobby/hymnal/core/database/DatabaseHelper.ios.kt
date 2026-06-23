package com.kobby.hymnal.core.database

import hymnal_cmp.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import platform.Foundation.*
import kotlinx.cinterop.*
import com.russhwolf.settings.Settings

actual class DatabaseHelper() {
    
    private val settings = Settings()
    
    @OptIn(ExperimentalResourceApi::class)
    actual suspend fun initializeDatabase(): String = withContext(Dispatchers.Default) {
        val databasePath = getDatabasePath()
        val installedDbVersion = settings.getInt("installed_db_version", 1)
        
        if (!isDatabaseInitialized() || installedDbVersion < CURRENT_DATABASE_VERSION) {
            copyDatabaseFromComposeResources(databasePath)
            settings.putInt("installed_db_version", CURRENT_DATABASE_VERSION)
        }
        
        databasePath
    }
    
    actual fun getDatabasePath(): String {
        // Use NSApplicationSupportDirectory as per SQLDelight convention
        val appSupportPath = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            true
        ).firstOrNull() as? String ?: throw RuntimeException("Could not access application support directory")
        
        return "$appSupportPath/databases/$DATABASE_NAME"
    }
    
    actual suspend fun isDatabaseInitialized(): Boolean = withContext(Dispatchers.Default) {
        val databasePath = getDatabasePath()
        NSFileManager.defaultManager.fileExistsAtPath(databasePath)
    }
    
    @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, ExperimentalResourceApi::class)
    private suspend fun copyDatabaseFromComposeResources(databasePath: String) = withContext(Dispatchers.Default) {
        try {
            println("Copying database from Compose resources to $databasePath")
            
            // Read database from Compose resources
            val sourceBytes = Res.readBytes("files/$DATABASE_NAME")
            
            // Ensure the parent directory exists
            val parentDir = (databasePath as NSString).stringByDeletingLastPathComponent
            val fileManager = NSFileManager.defaultManager
            
            if (!fileManager.fileExistsAtPath(parentDir)) {
                memScoped {
                    val error = alloc<ObjCObjectVar<NSError?>>()
                    val success = fileManager.createDirectoryAtPath(
                        path = parentDir,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = error.ptr
                    )
                    if (!success) {
                        throw RuntimeException("Failed to create database directory: ${error.value?.localizedDescription}")
                    }
                    println("Database directory created at $parentDir")
                }
            }
            
            // Remove existing file if it exists
            if (fileManager.fileExistsAtPath(databasePath)) {
                memScoped {
                    val error = alloc<ObjCObjectVar<NSError?>>()
                    fileManager.removeItemAtPath(databasePath, error.ptr)
                }
            }
            
            // Write the database file
            memScoped {
                val success = NSData.create(
                    bytes = allocArrayOf(sourceBytes),
                    length = sourceBytes.size.toULong()
                ).writeToFile(databasePath, true)
                
                if (!success) {
                    throw RuntimeException("Failed to write database file to $databasePath")
                }
            }
            
            println("Database copied successfully from Compose resources to $databasePath")
        } catch (e: Exception) {
            throw RuntimeException("Failed to copy database from Compose resources: ${e.message}", e)
        }
    }
}
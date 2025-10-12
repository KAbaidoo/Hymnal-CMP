package com.kobby.hymnal.core.database

import com.kobby.hymnal.composeApp.database.HymnDatabase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object DatabaseManager {
    private var database: HymnDatabase? = null
    private var repository: HymnRepository? = null
    private val mutex = Mutex()
    
    suspend fun initialize(driverFactory: DriverFactory) = mutex.withLock {
        if (database == null) {
            database = createDatabase(driverFactory)
            repository = HymnRepository(database!!)
        }
    }
    
    fun getRepository(): HymnRepository {
        return repository ?: throw IllegalStateException("Database not initialized. Call initialize() first.")
    }
    
    fun getDatabase(): HymnDatabase {
        return database ?: throw IllegalStateException("Database not initialized. Call initialize() first.")
    }
}
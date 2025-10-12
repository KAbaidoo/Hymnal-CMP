package com.kobby.hymnal.core.database

expect class DatabaseHelper {
    suspend fun initializeDatabase(): String
    fun getDatabasePath(): String
    suspend fun isDatabaseInitialized(): Boolean
}
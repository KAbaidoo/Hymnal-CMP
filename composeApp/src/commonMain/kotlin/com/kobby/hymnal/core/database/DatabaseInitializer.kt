package com.kobby.hymnal.core.database

expect class DatabaseInitializer {
    suspend fun initialize()
    fun getRepository(): HymnRepository
}
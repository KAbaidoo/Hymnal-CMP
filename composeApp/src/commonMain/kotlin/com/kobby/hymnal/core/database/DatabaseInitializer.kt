package com.kobby.hymnal.core.database

expect class DatabaseInitializer {
    fun getRepository(): HymnRepository
}
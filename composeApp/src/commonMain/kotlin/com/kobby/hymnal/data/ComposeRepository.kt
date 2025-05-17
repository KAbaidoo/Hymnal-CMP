package com.kobby.hymnal.data

interface ComposeRepository {
    fun welcome(): String
}

class ComposeRepositoryImpl(
//    private val databaseClient: DatabaseClient
) : ComposeRepository {
    override fun welcome(): String {
        return "THIS IS COMPOSE MULTIPLATFORM."
    }
}
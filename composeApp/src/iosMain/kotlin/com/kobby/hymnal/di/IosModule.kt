package com.kobby.hymnal.di

import com.kobby.hymnal.core.database.DatabaseHelper
import com.kobby.hymnal.core.database.DatabaseInitializer
import com.kobby.hymnal.core.database.DriverFactory
import com.kobby.hymnal.core.database.createDatabase
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val iosModule = module {
    single<DriverFactory> { DriverFactory() }
    single { runBlocking { createDatabase(get<DriverFactory>()) } }
    single<DatabaseHelper> { DatabaseHelper() }
    single<DatabaseInitializer> { DatabaseInitializer() }
}
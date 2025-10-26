package com.kobby.hymnal.di

import com.kobby.hymnal.core.database.DatabaseHelper
import com.kobby.hymnal.core.database.DatabaseInitializer
import com.kobby.hymnal.core.database.DriverFactory
import com.kobby.hymnal.core.database.createDatabase
import com.kobby.hymnal.core.sharing.ShareManager
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single<DriverFactory> { DriverFactory(androidContext()) }
    single { runBlocking { createDatabase(get<DriverFactory>()) } }
    single<DatabaseHelper> { DatabaseHelper(androidContext()) }
    single<DatabaseInitializer> { DatabaseInitializer(androidContext()) }
    single<ShareManager> { ShareManager(androidContext()) }
}
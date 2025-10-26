package com.kobby.hymnal.di

import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.core.database.DriverFactory
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.core.database.createDatabase
import org.koin.dsl.module

val databaseModule = module {
    single<HymnRepository> { HymnRepository(get()) }
}
package com.kobby.hymnal.di

import com.kobby.hymnal.core.config.IosRemoteConfigManager
import com.kobby.hymnal.core.config.RemoteConfigManager
import com.kobby.hymnal.core.database.DatabaseHelper
import com.kobby.hymnal.core.database.DatabaseInitializer
import com.kobby.hymnal.core.database.DriverFactory
import com.kobby.hymnal.core.database.createDatabase
import com.kobby.hymnal.core.review.IosReviewManager
import com.kobby.hymnal.core.review.ReviewManager
import com.kobby.hymnal.core.sharing.ShareManager
import com.kobby.hymnal.core.update.IosUpdateManager
import com.kobby.hymnal.core.update.UpdateManager
import kotlinx.coroutines.runBlocking
import org.koin.dsl.module

val iosModule = module {
    single<DriverFactory> { DriverFactory() }
    single { runBlocking { createDatabase(get<DriverFactory>()) } }
    single<DatabaseHelper> { DatabaseHelper() }
    single<DatabaseInitializer> { DatabaseInitializer() }
    single<ShareManager> { ShareManager() }
    single<ReviewManager> { IosReviewManager() }
    single<UpdateManager> { IosUpdateManager() }
    single<RemoteConfigManager> { IosRemoteConfigManager() }
}
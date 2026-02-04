package com.kobby.hymnal.di

import com.kobby.hymnal.core.crashlytics.CrashlyticsManager
import com.kobby.hymnal.core.crashlytics.createCrashlyticsManager
import org.koin.dsl.module

val crashlyticsModule = module {
    single<CrashlyticsManager> { createCrashlyticsManager() }
}

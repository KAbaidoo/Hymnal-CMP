package com.kobby.hymnal.di

import com.kobby.hymnal.ShowOnboarding
import com.kobby.hymnal.core.settings.FontSettingsManager
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val settingsModule = module {
    single { Settings() }
    single { FontSettingsManager(get()) }
    single { ShowOnboarding(get()) }
}
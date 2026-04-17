package com.kobby.hymnal.di

import org.koin.dsl.module

/**
 * Empty module as UpdateManager is now provided via platform-specific modules 
 * to allow proper dependency injection of Context on Android.
 */
val updateModule = module {
}

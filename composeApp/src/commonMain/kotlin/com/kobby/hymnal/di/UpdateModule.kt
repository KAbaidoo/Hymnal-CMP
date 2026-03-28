package com.kobby.hymnal.di

import com.kobby.hymnal.core.update.createUpdateManager
import org.koin.dsl.module

val updateModule = module {
    single { createUpdateManager() }
}

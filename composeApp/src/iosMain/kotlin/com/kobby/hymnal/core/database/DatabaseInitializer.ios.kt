package com.kobby.hymnal.core.database

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseInitializer : KoinComponent {
    private val hymnRepository: HymnRepository by inject()
    
    actual fun getRepository(): HymnRepository {
        return hymnRepository
    }
}
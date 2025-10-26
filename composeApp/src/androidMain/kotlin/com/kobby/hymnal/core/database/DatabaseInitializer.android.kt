package com.kobby.hymnal.core.database

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DatabaseInitializer(private val context: Context) : KoinComponent {
    private val hymnRepository: HymnRepository by inject()
    
    actual fun getRepository(): HymnRepository {
        return hymnRepository
    }
}
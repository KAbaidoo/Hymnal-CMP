package com.kobby.hymnal

import androidx.compose.ui.window.ComposeUIViewController
import com.kobby.hymnal.core.database.DatabaseInitializer

fun MainViewController() = ComposeUIViewController { 
    val databaseInitializer = DatabaseInitializer()
    HymnalApp(databaseInitializer) 
}
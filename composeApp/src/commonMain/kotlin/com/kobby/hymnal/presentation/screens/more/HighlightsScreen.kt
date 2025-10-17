package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.DatabaseManager
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.screens.more.components.HighlightsContent

class HighlightsScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var repository by remember { mutableStateOf<HymnRepository?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        
        // Initialize repository
        LaunchedEffect(Unit) {
            try {
                val database = DatabaseManager.getDatabase()
                repository = HymnRepository(database)
                isLoading = false
            } catch (e: Exception) {
                error = "Failed to load highlights: ${e.message}"
                isLoading = false
            }
        }
        
        HighlightsContent(
            isLoading = isLoading,
            error = error,
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping all screens
                while (navigator.canPop) {
                    navigator.pop()
                }
            }
        )
    }
}
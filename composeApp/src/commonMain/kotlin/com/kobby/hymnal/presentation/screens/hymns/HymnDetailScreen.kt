package com.kobby.hymnal.presentation.screens.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.database.DatabaseManager
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.components.DetailScreen
import kotlinx.coroutines.launch

data class HymnDetailScreen(private val hymn: Hymn) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        var repository by remember { mutableStateOf<HymnRepository?>(null) }
        var isFavorite by remember { mutableStateOf(false) }
        
        // Initialize repository
        LaunchedEffect(Unit) {
            val database = DatabaseManager.getDatabase()
            repository = HymnRepository(database)
        }
        
        // Check if hymn is favorite
        LaunchedEffect(repository) {
            repository?.let { repo ->
                isFavorite = repo.isFavorite(hymn.id)
            }
        }
        
        // Add to history when screen opens
        LaunchedEffect(hymn.id) {
            repository?.addToHistory(hymn.id)
        }
        
        DetailScreen(
            hymn = hymn,
            isFavorite = isFavorite,
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping all screens
                while (navigator.canPop) {
                    navigator.pop()
                }
            },
            onFavoriteClick = {
                scope.launch {
                    repository?.let { repo ->
                        if (isFavorite) {
                            repo.removeFromFavorites(hymn.id)
                        } else {
                            repo.addToFavorites(hymn.id)
                        }
                        isFavorite = !isFavorite
                    }
                }
            },
            onFontSettingsClick = {
                // TODO: Implement font settings
            },
            onShareClick = {
                // TODO: Implement sharing
            }
        )
    }
}
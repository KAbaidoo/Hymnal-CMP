package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.flowOf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.DatabaseManager
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import com.kobby.hymnal.presentation.screens.more.components.HistoryContent

class HistoryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var repository by remember { mutableStateOf<HymnRepository?>(null) }
        var searchText by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        
        // Initialize repository
        LaunchedEffect(Unit) {
            try {
                val database = DatabaseManager.getDatabase()
                repository = HymnRepository(database)
                isLoading = false
            } catch (e: Exception) {
                error = "Failed to load history: ${e.message}"
                isLoading = false
            }
        }
        
        // Get recent hymns from repository if available
        val recentHymnsData by (repository?.getRecentHymns(50)
            ?: flowOf(emptyList())).collectAsState(initial = emptyList())
        
        // Convert GetRecentHymns to Hymn objects
        val recentHymns = remember(recentHymnsData) {
            recentHymnsData.map { data ->
                com.kobby.hymnal.composeApp.database.Hymn(
                    id = data.id,
                    number = data.number,
                    title = data.title,
                    category = data.category,
                    content = data.content,
                    created_at = data.created_at
                )
            }
        }
        
        val filteredHymns = remember(recentHymns, searchText) {
            if (searchText.isBlank()) {
                recentHymns
            } else {
                recentHymns.filter { hymn ->
                    hymn.title?.contains(searchText, ignoreCase = true) == true ||
                    hymn.number.toString().contains(searchText) ||
                    hymn.content?.contains(searchText, ignoreCase = true) == true
                }
            }
        }
        
        HistoryContent(
            hymns = filteredHymns,
            searchText = searchText,
            isLoading = isLoading,
            error = error,
            onSearchTextChanged = { searchText = it },
            onItemClick = { hymn ->
                navigator.push(HymnDetailScreen(hymn))
            },
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping all screens
                while (navigator.canPop) {
                    navigator.pop()
                }
            },
            onClearHistory = {
                // TODO: Implement clear history
            }
        )
    }
}
package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import com.kobby.hymnal.presentation.screens.more.components.HistoryContent
import org.koin.compose.koinInject

class HistoryScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: HymnRepository = koinInject()
        var searchText by remember { mutableStateOf("") }
        
        // Get recent hymns from repository
        val recentHymnsData by repository.getRecentHymns(50).collectAsState(initial = emptyList())
        
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
            isLoading = false,
            error = null,
            onSearchTextChanged = { searchText = it },
            onItemClick = { hymn ->
                navigator.push(HymnDetailScreen(hymn))
            },
            onBackClick = { navigator.pop() },
            onHomeClick = { 
                // Navigate to home by popping until we reach HomeScreen or we can't pop anymore
                while (navigator.canPop) {
                    navigator.pop()
                    // Check if current screen is HomeScreen by trying to find it in the stack
                    if (navigator.lastItem is com.kobby.hymnal.presentation.screens.home.HomeScreen) {
                        break
                    }
                }
            },
            onClearHistory = {
                // TODO: Implement clear history
            }
        )
    }
}
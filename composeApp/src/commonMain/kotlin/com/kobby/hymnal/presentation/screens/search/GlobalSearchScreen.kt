package com.kobby.hymnal.presentation.screens.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.DatabaseManager
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.components.ListScreen
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class GlobalSearchScreen : Screen {
    @OptIn(FlowPreview::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var repository by remember { mutableStateOf<HymnRepository?>(null) }
        var searchText by remember { mutableStateOf("") }
        val searchFlow = remember { MutableStateFlow("") }
        
        // Initialize repository
        LaunchedEffect(Unit) {
            val database = DatabaseManager.getDatabase()
            repository = HymnRepository(database)
        }
        
        // Update search flow when text changes
        LaunchedEffect(searchText) {
            searchFlow.value = searchText
        }
        
        repository?.let { repo ->
            // Search with debounce
            val searchResults by remember(repo) {
                searchFlow
                    .debounce(300) // Wait for 300ms pause in typing
                    .filter { it.isNotBlank() && it.length >= 2 } // Only search if query is meaningful
                    .flatMapLatest { query ->
                        repo.searchHymns(query)
                    }
            }.collectAsState(initial = emptyList())
            
            // Show all hymns if search is empty/short
            val allHymns by repo.getAllHymns().collectAsState(initial = emptyList())
            
            val displayedHymns = if (searchText.isBlank() || searchText.length < 2) {
                allHymns
            } else {
                searchResults
            }
            
            ListScreen(
                titleCollapsed = "Search Hymns",
                titleExpanded = "Search\nHymns",
                items = displayedHymns,
                searchText = searchText,
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
                }
            )
        }
    }
}
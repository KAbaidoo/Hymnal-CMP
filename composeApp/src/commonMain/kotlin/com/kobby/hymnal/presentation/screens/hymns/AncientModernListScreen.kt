package com.kobby.hymnal.presentation.screens.hymns

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
import kotlinx.coroutines.flow.filter

class AncientModernListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var repository by remember { mutableStateOf<HymnRepository?>(null) }
        var searchText by remember { mutableStateOf("") }
        
        // Initialize repository
        LaunchedEffect(Unit) {
            val database = DatabaseManager.getDatabase()
            repository = HymnRepository(database)
        }
        
        repository?.let { repo ->
            val hymns by repo.getHymnsByCategory(HymnRepository.CATEGORY_ANCIENT_MODERN)
                .collectAsState(initial = emptyList())
            
            val filteredHymns = remember(hymns, searchText) {
                if (searchText.isBlank()) {
                    hymns
                } else {
                    hymns.filter { hymn ->
                        hymn.title?.contains(searchText, ignoreCase = true) == true ||
                        hymn.number.toString().contains(searchText) ||
                        hymn.content?.contains(searchText, ignoreCase = true) == true
                    }
                }
            }
            
            ListScreen(
                titleCollapsed = "Ancient & Modern",
                titleExpanded = "Ancient\n& Modern",
                items = filteredHymns,
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
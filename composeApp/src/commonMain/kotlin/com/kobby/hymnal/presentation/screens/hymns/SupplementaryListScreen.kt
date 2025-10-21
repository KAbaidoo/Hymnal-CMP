package com.kobby.hymnal.presentation.screens.hymns

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
import com.kobby.hymnal.presentation.screens.hymns.components.SupplementaryListContent
import kotlinx.coroutines.flow.filter
import org.koin.compose.koinInject

class SupplementaryListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: HymnRepository = koinInject()
        var searchText by remember { mutableStateOf("") }
        
        // Get hymns from repository
        val hymns by repository.getHymnsByCategory(HymnRepository.CATEGORY_SUPPLEMENTARY).collectAsState(initial = emptyList())
        
        val filteredHymns = remember(hymns, searchText) {
            if (searchText.isBlank()) {
                hymns
            } else {
                hymns.filter { hymn ->
                    hymn.title?.contains(searchText, ignoreCase = true) == true ||
                    hymn.number.toString().contains(searchText)
                }
            }
        }
        
        SupplementaryListContent(
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
            }
        )
    }
}
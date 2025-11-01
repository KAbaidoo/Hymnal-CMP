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
import com.kobby.hymnal.core.database.HymnRepository
import com.kobby.hymnal.presentation.components.ListScreen
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.search_hymns
import hymnal_cmp.composeapp.generated.resources.search_hymns_multiline
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

class GlobalSearchScreen : Screen {
    @OptIn(FlowPreview::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: HymnRepository = koinInject()
        var searchText by remember { mutableStateOf("") }
        val searchFlow = remember { MutableStateFlow("") }
        
        // Update search flow when text changes
        LaunchedEffect(searchText) {
            searchFlow.value = searchText
        }
        // Search with debounce
        val searchResults by remember(repository) {
            searchFlow
                .debounce(300) // Wait for 300ms pause in typing
                .filter { it.isNotBlank() && it.length >= 2 } // Only search if query is meaningful
                .flatMapLatest { query ->
                    repository.searchHymns(query)
                }
        }.collectAsState(initial = emptyList())
        
        // Show all hymns if search is empty/short
        val allHymns by repository.getAllHymns().collectAsState(initial = emptyList())
            
        val displayedHymns = if (searchText.isBlank() || searchText.length < 2) {
            allHymns
        } else {
            searchResults
        }
        
        ListScreen(
            titleCollapsed = stringResource(Res.string.search_hymns),
            titleExpanded = stringResource(Res.string.search_hymns_multiline),
            items = displayedHymns,
            searchText = searchText,
            onSearchTextChanged = { searchText = it },
            onItemClick = { hymn ->
                navigator.push(HymnDetailScreen(hymnId = hymn.id))
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
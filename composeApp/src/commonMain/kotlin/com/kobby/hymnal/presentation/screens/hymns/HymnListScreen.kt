package com.kobby.hymnal.presentation.screens.hymns

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.SafeHymnRepository
import com.kobby.hymnal.presentation.screens.hymns.components.HymnListContent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import org.koin.compose.koinInject

class HymnListScreen(
    private val category: String,
    private val titleCollapsed: String,
    private val titleExpanded: String,
    private val source: String = "category_list"
) : Screen {
    override val key = uniqueScreenKey
    
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: SafeHymnRepository = koinInject()
        var searchText by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        
        // Get hymns from repository
        val hymns by remember(category, repository) {
            repository.getHymnsByCategory(category)
                .catch { e ->
                    error = e.message ?: "An error occurred loading hymns"
                }
        }.collectAsState(initial = emptyList())
        
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
        
        HymnListContent(
            titleCollapsed = titleCollapsed,
            titleExpanded = titleExpanded,
            hymns = filteredHymns,
            searchText = searchText,
            isLoading = isLoading,
            error = error,
            onSearchTextChanged = { searchText = it },
            onItemClick = { hymn ->
                navigator.push(
                    HymnDetailScreen(
                        hymnId = hymn.id,
                        source = source
                    )
                )
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

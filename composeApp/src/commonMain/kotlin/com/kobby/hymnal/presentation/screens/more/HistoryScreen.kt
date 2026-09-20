package com.kobby.hymnal.presentation.screens.more

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import kotlinx.coroutines.launch
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.SafeHymnRepository
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import com.kobby.hymnal.presentation.screens.hymns.HymnDetailScreen
import com.kobby.hymnal.presentation.screens.more.components.HistoryContent
import org.koin.compose.koinInject
import kotlinx.coroutines.flow.catch

class HistoryScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: SafeHymnRepository = koinInject()
        val traceManager: TraceManager = koinInject()
        val coroutineScope = rememberCoroutineScope()
        var searchText by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        
        // Get recent hymns from repository
        val recentHymns by remember(repository) {
            repository.getRecentHymns(50)
                .catch { e ->
                    error = e.message ?: "An error occurred loading history"
                }
        }.collectAsState(initial = emptyList())
        
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
            error = error,
            onSearchTextChanged = { searchText = it },
            onItemClick = { hymn ->
                traceManager.track(
                    TraceEvents.HISTORY_MANAGEMENT,
                    traceParams("action" to "open_item", "hymn_id" to hymn.id)
                )
                navigator.push(HymnDetailScreen(hymnId = hymn.id, source = "history"))
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
                coroutineScope.launch {
                    traceManager.track(
                        TraceEvents.HISTORY_MANAGEMENT,
                        traceParams("action" to "clear_all", "history_count_before_clear" to filteredHymns.size)
                    )
                    repository.clearHistory()
                }
            }
        )
    }
}

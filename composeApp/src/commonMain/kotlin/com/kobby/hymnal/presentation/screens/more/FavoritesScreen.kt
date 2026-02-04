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
import com.kobby.hymnal.presentation.screens.more.components.FavoritesContent
import org.koin.compose.koinInject

class FavoritesScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repository: HymnRepository = koinInject()
        var searchText by remember { mutableStateOf("") }
        
        // All users can access favorites now - no gates!
        val favoriteHymns by repository.getFavoriteHymns().collectAsState(initial = emptyList())

        val filteredHymns = remember(favoriteHymns, searchText) {
            if (searchText.isBlank()) {
                favoriteHymns
            } else {
                favoriteHymns.filter { hymn ->
                    hymn.title?.contains(searchText, ignoreCase = true) == true ||
                    hymn.number.toString().contains(searchText) ||
                    hymn.content?.contains(searchText, ignoreCase = true) == true
                }
            }
        }

        FavoritesContent(
            hymns = filteredHymns,
            searchText = searchText,
            isLoading = false,
            error = null,
            onSearchTextChanged = { searchText = it },
            onItemClick = { hymn ->
                navigator.push(HymnDetailScreen(hymnId = hymn.id))
            },
            onBackClick = { navigator.pop() },
            onHomeClick = {
                // Navigate to home by popping until we reach HomeScreen or we can't pop anymore
                while (navigator.canPop) {
                    navigator.pop()
                    if (navigator.lastItem is com.kobby.hymnal.presentation.screens.home.HomeScreen) {
                        break
                    }
                }
            }
        )
    }
}

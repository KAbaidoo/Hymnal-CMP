package com.kobby.hymnal.presentation.screens.more.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.components.ListScreen

@Composable
fun FavoritesContent(
    hymns: List<Hymn>,
    searchText: String,
    isLoading: Boolean,
    error: String?,
    onSearchTextChanged: (String) -> Unit,
    onItemClick: (Hymn) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        
        error != null -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        
        hymns.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite hymns yet.\nTap the heart icon on any hymn to add it to favorites.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        else -> {
            ListScreen(
                titleCollapsed = "Favorites",
                titleExpanded = "Favorites",
                items = hymns,
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onItemClick = onItemClick,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick
            )
        }
    }
}
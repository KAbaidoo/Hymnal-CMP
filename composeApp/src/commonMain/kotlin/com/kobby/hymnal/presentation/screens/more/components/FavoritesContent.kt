package com.kobby.hymnal.presentation.screens.more.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.components.ContentScreen
import com.kobby.hymnal.presentation.components.ListItem
import com.kobby.hymnal.presentation.components.ListScreen
import com.kobby.hymnal.presentation.components.SearchTextField
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.favorites
import hymnal_cmp.composeapp.generated.resources.no_favorite_hymns
import hymnal_cmp.composeapp.generated.resources.search_placeholder
import org.jetbrains.compose.resources.stringResource

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
        
        else -> {
            FavoritesListScreen(
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

@Composable
private fun FavoritesListScreen(
    items: List<Hymn>,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onItemClick: (Hymn) -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
){
    ContentScreen(
        titleCollapsed = stringResource(Res.string.favorites),
        titleExpanded = stringResource(Res.string.favorites),
        actionButtons = null,
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                if (items.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(Res.string.no_favorite_hymns),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    items(items) { hymn ->
                        ListItem(
                            title = "${hymn.number}. ${hymn.title ?: "Untitled"}",
                            onClick = { onItemClick(hymn) }
                        )
                    }
                }
            }
        },
        bottomBar = { 
            SearchTextField(
                modifier = Modifier.fillMaxWidth(), 
                searchText = searchText, 
                onTextChanged = onSearchTextChanged, 
                placeholderText = stringResource(Res.string.search_placeholder)
            ) 
        },
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}
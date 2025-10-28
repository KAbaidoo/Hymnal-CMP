package com.kobby.hymnal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kobby.hymnal.composeApp.database.Hymn
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.search_placeholder
import org.jetbrains.compose.resources.stringResource

private fun formatHymnTitle(hymn: Hymn): String {
    return if (hymn.category == "canticles") {
        hymn.title ?: "Untitled"
    } else {
        "${hymn.number}. ${hymn.title ?: "Untitled"}"
    }
}

@Composable
fun ListScreen(
    titleCollapsed: String,
    titleExpanded: String,
    items: List<Hymn>,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onItemClick: (Hymn) -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    actionButtons: @Composable() (() -> Unit?)? = null
){
    ContentScreen(
        titleCollapsed = titleCollapsed,
        titleExpanded = titleExpanded,
        actionButtons = actionButtons,
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                items(items) { hymn ->
                    ListItem(
                        title = formatHymnTitle(hymn),
                        onClick = { onItemClick(hymn) }
                    )
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
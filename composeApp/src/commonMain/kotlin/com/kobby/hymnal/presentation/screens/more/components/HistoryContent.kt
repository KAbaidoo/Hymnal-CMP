package com.kobby.hymnal.presentation.screens.more.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.presentation.components.ContentScreen
import com.kobby.hymnal.presentation.components.ListScreen
import com.kobby.hymnal.presentation.components.ListItem
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.delete_bin_line
import hymnal_cmp.composeapp.generated.resources.cd_clear_history
import hymnal_cmp.composeapp.generated.resources.clear_history
import hymnal_cmp.composeapp.generated.resources.clear_history_message
import hymnal_cmp.composeapp.generated.resources.cancel
import hymnal_cmp.composeapp.generated.resources.clear
import hymnal_cmp.composeapp.generated.resources.clear_all
import hymnal_cmp.composeapp.generated.resources.history
import hymnal_cmp.composeapp.generated.resources.no_recent_hymns
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import com.kobby.hymnal.theme.YellowAccent

private fun formatHymnTitle(hymn: Hymn): String {
    return if (hymn.category == "canticles") {
        hymn.title ?: "Untitled"
    } else {
        "${hymn.number}. ${hymn.title ?: "Untitled"}"
    }
}
@Composable
fun HistoryContent(
    hymns: List<Hymn>,
    searchText: String,
    isLoading: Boolean,
    error: String?,
    onSearchTextChanged: (String) -> Unit,
    onItemClick: (Hymn) -> Unit,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onClearHistory: () -> Unit = {}
) {
    var showClearDialog by remember { mutableStateOf(false) }

    // Confirmation dialog for clearing history
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(stringResource(Res.string.clear_history)) },
            text = { Text(stringResource(Res.string.clear_history_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showClearDialog = false
                    }
                ) {
                    Text(stringResource(Res.string.clear))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }

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
            HistoryListScreen(
                items = hymns,
                searchText = searchText,
                onSearchTextChanged = onSearchTextChanged,
                onItemClick = onItemClick,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                actionButtons = {
                    TextButton(onClick = { showClearDialog = true }) {
                        Text(
                            text = stringResource(Res.string.clear_all),
                            style = MaterialTheme.typography.bodyMedium,
                            color = YellowAccent
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryListScreen(
    items: List<Hymn>,
    searchText: String,
    onSearchTextChanged: (String) -> Unit = {},
    onItemClick: (Hymn) -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    actionButtons: @Composable () -> Unit = {}
){
    ContentScreen(
        titleCollapsed = stringResource(Res.string.history),
        titleExpanded = stringResource(Res.string.history),
        actionButtons = actionButtons,
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
                                text = stringResource(Res.string.no_recent_hymns),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    items(items) { hymn ->
                        ListItem(
                            title = formatHymnTitle(hymn),
                            onClick = { onItemClick(hymn) }
                        )
                    }
                }
            }
        },
        bottomBar = {},
        onBackClick = onBackClick,
        onHomeClick = onHomeClick
    )
}
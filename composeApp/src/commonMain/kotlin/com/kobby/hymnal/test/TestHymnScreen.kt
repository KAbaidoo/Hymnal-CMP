package com.kobby.hymnal.test

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kobby.hymnal.core.database.DatabaseManager
import com.kobby.hymnal.debug.DatabaseInspectorScreen
import com.kobby.hymnal.core.database.HymnRepository
import kotlinx.coroutines.launch

class TestHymnScreen : Screen {
    
    @Composable
    override fun Content() {
        var hymns by remember { mutableStateOf(emptyList<com.kobby.hymnal.composeApp.database.Hymn>()) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        
        val scope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        
        LaunchedEffect(Unit) {
            scope.launch {
                try {
                    val repository = DatabaseManager.getRepository()
                    repository.getAllHymns().collect { hymnList ->
                        hymns = hymnList
                        isLoading = false
                    }
                } catch (e: Exception) {
                    error = e.message
                    isLoading = false
                }
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "Hymn Database Test",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
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
                    Text(
                        text = "Error: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                
                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Found ${hymns.size} hymns",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Button(
                            onClick = { navigator.push(DatabaseInspectorScreen()) }
                        ) {
                            Text("Database Inspector")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn {
                        items(hymns.take(10)) { hymn ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Hymn ${hymn.number}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (hymn.title?.isNotEmpty() == true) {
                                        Text(
                                            text = hymn.title,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "Category: ${hymn.category}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.kobby.hymnal.debug

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.database.HymnRepository
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import kotlin.time.measureTime
import hymnal_cmp.composeapp.generated.resources.Res
import hymnal_cmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

class DatabaseInspectorScreen : Screen {
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val repository: HymnRepository = koinInject()
        var stats by remember { mutableStateOf<DatabaseStats?>(null) }
        var searchQuery by remember { mutableStateOf("") }
        var searchResults by remember { mutableStateOf<List<Hymn>>(emptyList()) }
        var searchTime by remember { mutableStateOf(0L) }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        
        val scope = rememberCoroutineScope()
        
        LaunchedEffect(Unit) {
            scope.launch {
                try {
                    stats = collectDatabaseStats(repository)
                } catch (e: Exception) {
                    error = e.message
                }
            }
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = stringResource(Res.string.database_inspector),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            when {
                error != null -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = stringResource(Res.string.error_message, error ?: ""),
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                stats == null -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Database Statistics
                        item {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.database_statistics),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    
                                    StatRow(stringResource(Res.string.total_hymns), stats!!.totalHymns.toString())
                                    StatRow(stringResource(Res.string.ancient_modern), stats!!.ancientModernCount.toString())
                                    StatRow(stringResource(Res.string.supplementary), stats!!.supplementaryCount.toString())
                                    StatRow(stringResource(Res.string.favorites), stats!!.favoritesCount.toString())
                                    StatRow(stringResource(Res.string.history_entries), stats!!.historyCount.toString())
                                    StatRow(stringResource(Res.string.highlights), stats!!.highlightsCount.toString())
                                }
                            }
                        }
                        
                        // Search Performance Test
                        item {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.search_performance_test),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    
                                    OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        label = { Text(stringResource(Res.string.search_query)) },
                                        trailingIcon = {
                                            IconButton(
                                                onClick = {
                                                    scope.launch {
                                                        isLoading = true
                                                        try {
                                                            // repository is already available from outer scope
                                                            val duration = measureTime {
                                                                repository.searchHymns(searchQuery).collect { results ->
                                                                    searchResults = results
                                                                }
                                                            }
                                                            searchTime = duration.inWholeMilliseconds
                                                        } catch (e: Exception) {
                                                            error = e.message
                                                        } finally {
                                                            isLoading = false
                                                        }
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.Search, contentDescription = stringResource(Res.string.cd_search))
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    
                                    if (searchTime > 0) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = stringResource(Res.string.search_completed_time, searchTime.toInt()),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = stringResource(Res.string.found_results, searchResults.size),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                    
                                    if (isLoading) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                                    }
                                }
                            }
                        }
                        
                        // Sample Data
                        item {
                            Card {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.sample_hymns),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    
                                    stats!!.sampleHymns.forEach { hymn ->
                                        HymnRow(hymn)
                                    }
                                }
                            }
                        }
                        
                        // Search Results
                        if (searchResults.isNotEmpty()) {
                            item {
                                Card {
                                    Column(
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.search_results_count, searchResults.size),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                    }
                                }
                            }
                            
                            items(searchResults.take(10)) { hymn ->
                                Card {
                                    HymnRow(hymn)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Composable
    private fun StatRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
    
    @Composable
    private fun HymnRow(hymn: Hymn) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(Res.string.hymn_number, hymn.number ?: 0),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = hymn.category.replace("_", " ").uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            if (hymn.title?.isNotEmpty() == true) {
                Text(
                    text = hymn.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = hymn.content?.take(50) + if ((hymn.content?.length ?: 0) > 50) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    private suspend fun collectDatabaseStats(repository: HymnRepository): DatabaseStats {
        val allHymns = repository.getAllHymns().collect { it }
        var totalHymns = 0
        var ancientModern = 0
        var supplementary = 0
        
        repository.getAllHymns().collect { hymns ->
            totalHymns = hymns.size
            ancientModern = hymns.count { it.category == "ancient_modern" }
            supplementary = hymns.count { it.category == "supplementary" }
        }
        
        val favorites = repository.getFavoriteHymns().collect { it }
        var favoritesCount = 0
        repository.getFavoriteHymns().collect { favoritesCount = it.size }
        
        // Get sample data
        val sampleHymns = mutableListOf<Hymn>()
        repository.getAllHymns().collect { hymns ->
            sampleHymns.addAll(hymns.take(5))
        }
        
        return DatabaseStats(
            totalHymns = totalHymns,
            ancientModernCount = ancientModern,
            supplementaryCount = supplementary,
            favoritesCount = favoritesCount,
            historyCount = 0, // Would need to implement proper counting
            highlightsCount = 0, // Would need to implement proper counting
            sampleHymns = sampleHymns
        )
    }
}

data class DatabaseStats(
    val totalHymns: Int,
    val ancientModernCount: Int,
    val supplementaryCount: Int,
    val favoritesCount: Int,
    val historyCount: Int,
    val highlightsCount: Int,
    val sampleHymns: List<Hymn>
)
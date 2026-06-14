package com.kobby.hymnal.core.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.composeApp.database.Hymn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class HymnRepository(private val database: HymnDatabase) {
    
    // Hymn queries
    fun getAllHymns(): Flow<List<Hymn>> {
        return database.hymnsQueries.getAllHymns()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }
    
    fun getHymnsByCategory(category: String): Flow<List<Hymn>> {
        return database.hymnsQueries.getHymnsByCategory(category)
            .asFlow()
            .mapToList(Dispatchers.Default)
    }
    
    suspend fun getHymnById(id: Long): Hymn? = withContext(Dispatchers.Default) {
        database.hymnsQueries.getHymnById(id)
            .executeAsOneOrNull()
    }
    
    suspend fun getHymnByNumber(number: Long, category: String): Hymn? = withContext(Dispatchers.Default) {
        database.hymnsQueries.getHymnByNumber(number, category)
            .executeAsOneOrNull()
    }
    
    suspend fun getRandomHymn(): Hymn? = withContext(Dispatchers.Default) {
        database.hymnsQueries.getRandomHymn()
            .executeAsOneOrNull()
    }
    
    fun searchHymns(query: String): Flow<List<Hymn>> {
        return database.hymnsQueries.searchHymns(query)
            .asFlow()
            .mapToList(Dispatchers.Default)
    }
    
    // Favorite queries
    fun getFavoriteHymns(): Flow<List<Hymn>> {
        return database.hymnsQueries.getFavoriteHymns()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }
    
    suspend fun addToFavorites(hymnId: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.addToFavorites(hymnId)
    }
    
    suspend fun removeFromFavorites(hymnId: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.removeFromFavorites(hymnId)
    }
    
    suspend fun isFavorite(hymnId: Long): Boolean = withContext(Dispatchers.Default) {
        database.hymnsQueries.isFavorite(hymnId)
            .executeAsOne()
    }
    
    // History queries
    fun getRecentHymns(limit: Long = 20) = database.hymnsQueries.getRecentHymns(limit)
        .asFlow()
        .mapToList(Dispatchers.Default)
    
    suspend fun addToHistory(hymnId: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.addToHistory(hymnId)
        trimHistoryToLimit()
    }
    
    suspend fun clearHistory() = withContext(Dispatchers.Default) {
        database.hymnsQueries.clearHistory()
    }
    
    private suspend fun trimHistoryToLimit() = withContext(Dispatchers.Default) {
        database.hymnsQueries.trimHistoryToLimit(HISTORY_LIMIT)
    }
    
    // Highlight queries
    suspend fun getHighlightsForHymn(hymnId: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.getHighlightsForHymn(hymnId)
            .executeAsList()
    }
    
    fun getHymnsWithHighlights(): Flow<List<Hymn>> {
        return database.hymnsQueries.getHymnsWithHighlights()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { results ->
                results.map { result ->
                    Hymn(
                        id = result.id,
                        number = result.number,
                        title = result.title,
                        category = result.category,
                        content = result.content,
                        created_at = result.created_at
                    )
                }
            }
    }
    
    suspend fun addHighlight(hymnId: Long, startIndex: Long, endIndex: Long, colorIndex: Long = 0) = withContext(Dispatchers.Default) {
        database.hymnsQueries.addHighlight(hymnId, startIndex, endIndex, colorIndex)
    }
    
    suspend fun updateHighlightColor(highlightId: Long, colorIndex: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.updateHighlightColor(colorIndex, highlightId)
    }
    
    suspend fun removeHighlight(highlightId: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.removeHighlight(highlightId)
    }
    
    suspend fun clearHighlightsForHymn(hymnId: Long) = withContext(Dispatchers.Default) {
        database.hymnsQueries.clearHighlightsForHymn(hymnId)
    }
    
    // Category helpers
    companion object {
        const val CATEGORY_ANCIENT_MODERN = "ancient_modern"
        const val CATEGORY_SUPPLEMENTARY = "supplementary"
        const val CATEGORY_CANTICLES = "canticles"
        const val CATEGORY_PSALMS = "psalms"
        
        // History management
        private const val HISTORY_LIMIT = 100L
    }
}
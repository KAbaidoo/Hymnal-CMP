package com.kobby.hymnal.core.database

import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.crashlytics.CrashlyticsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * Wrapper around HymnRepository that adds Crashlytics error reporting
 * for all database operations.
 */
class SafeHymnRepository(
    private val repository: HymnRepository,
    private val crashlytics: CrashlyticsManager
) {
    
    // Hymn queries
    fun getAllHymns(): Flow<List<Hymn>> {
        return repository.getAllHymns()
            .catch { e ->
                crashlytics.log("Error in getAllHymns")
                crashlytics.recordException(e)
                throw e
            }
    }
    
    fun getHymnsByCategory(category: String): Flow<List<Hymn>> {
        return repository.getHymnsByCategory(category)
            .catch { e ->
                crashlytics.log("Error in getHymnsByCategory: $category")
                crashlytics.setCustomKey("category", category)
                crashlytics.recordException(e)
                throw e
            }
    }
    
    suspend fun getHymnById(id: Long): Hymn? {
        return try {
            repository.getHymnById(id)
        } catch (e: Exception) {
            crashlytics.log("Error in getHymnById: $id")
            crashlytics.setCustomKey("hymn_id", id.toInt())
            crashlytics.recordException(e)
            null
        }
    }
    
    suspend fun getHymnByNumber(number: Long, category: String): Hymn? {
        return try {
            repository.getHymnByNumber(number, category)
        } catch (e: Exception) {
            crashlytics.log("Error in getHymnByNumber: $number, category: $category")
            crashlytics.setCustomKey("hymn_number", number.toInt())
            crashlytics.setCustomKey("category", category)
            crashlytics.recordException(e)
            null
        }
    }
    
    suspend fun getRandomHymn(): Hymn? {
        return try {
            repository.getRandomHymn()
        } catch (e: Exception) {
            crashlytics.log("Error in getRandomHymn")
            crashlytics.recordException(e)
            null
        }
    }
    
    fun searchHymns(query: String): Flow<List<Hymn>> {
        return repository.searchHymns(query)
            .catch { e ->
                crashlytics.log("Error in searchHymns: $query")
                crashlytics.setCustomKey("search_query", query)
                crashlytics.recordException(e)
                throw e
            }
    }
    
    // Favorite queries
    fun getFavoriteHymns(): Flow<List<Hymn>> {
        return repository.getFavoriteHymns()
            .catch { e ->
                crashlytics.log("Error in getFavoriteHymns")
                crashlytics.recordException(e)
                throw e
            }
    }
    
    suspend fun addToFavorites(hymnId: Long) {
        try {
            repository.addToFavorites(hymnId)
        } catch (e: Exception) {
            crashlytics.log("Error in addToFavorites: $hymnId")
            crashlytics.setCustomKey("hymn_id", hymnId.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
    
    suspend fun removeFromFavorites(hymnId: Long) {
        try {
            repository.removeFromFavorites(hymnId)
        } catch (e: Exception) {
            crashlytics.log("Error in removeFromFavorites: $hymnId")
            crashlytics.setCustomKey("hymn_id", hymnId.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
    
    suspend fun isFavorite(hymnId: Long): Boolean {
        return try {
            repository.isFavorite(hymnId)
        } catch (e: Exception) {
            crashlytics.log("Error in isFavorite: $hymnId")
            crashlytics.setCustomKey("hymn_id", hymnId.toInt())
            crashlytics.recordException(e)
            false
        }
    }
    
    // History queries
    fun getRecentHymns(limit: Long = 20): Flow<List<Hymn>> {
        return repository.getRecentHymns(limit)
            .catch { e ->
                crashlytics.log("Error in getRecentHymns: limit=$limit")
                crashlytics.setCustomKey("history_limit", limit.toInt())
                crashlytics.recordException(e)
                throw e
            }
    }
    
    suspend fun addToHistory(hymnId: Long) {
        try {
            repository.addToHistory(hymnId)
        } catch (e: Exception) {
            crashlytics.log("Error in addToHistory: $hymnId")
            crashlytics.setCustomKey("hymn_id", hymnId.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
    
    suspend fun clearHistory() {
        try {
            repository.clearHistory()
        } catch (e: Exception) {
            crashlytics.log("Error in clearHistory")
            crashlytics.recordException(e)
            throw e
        }
    }
    
    // Highlight queries
    suspend fun getHighlightsForHymn(hymnId: Long) = try {
        repository.getHighlightsForHymn(hymnId)
    } catch (e: Exception) {
        crashlytics.log("Error in getHighlightsForHymn: $hymnId")
        crashlytics.setCustomKey("hymn_id", hymnId.toInt())
        crashlytics.recordException(e)
        emptyList()
    }
    
    fun getHymnsWithHighlights(): Flow<List<Hymn>> {
        return repository.getHymnsWithHighlights()
            .catch { e ->
                crashlytics.log("Error in getHymnsWithHighlights")
                crashlytics.recordException(e)
                throw e
            }
    }
    
    suspend fun addHighlight(hymnId: Long, startIndex: Long, endIndex: Long, colorIndex: Long = 0) {
        try {
            repository.addHighlight(hymnId, startIndex, endIndex, colorIndex)
        } catch (e: Exception) {
            crashlytics.log("Error in addHighlight: hymnId=$hymnId, start=$startIndex, end=$endIndex")
            crashlytics.setCustomKey("hymn_id", hymnId.toInt())
            crashlytics.setCustomKey("start_index", startIndex.toInt())
            crashlytics.setCustomKey("end_index", endIndex.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
    
    suspend fun updateHighlightColor(highlightId: Long, colorIndex: Long) {
        try {
            repository.updateHighlightColor(highlightId, colorIndex)
        } catch (e: Exception) {
            crashlytics.log("Error in updateHighlightColor: highlightId=$highlightId")
            crashlytics.setCustomKey("highlight_id", highlightId.toInt())
            crashlytics.setCustomKey("color_index", colorIndex.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
    
    suspend fun removeHighlight(highlightId: Long) {
        try {
            repository.removeHighlight(highlightId)
        } catch (e: Exception) {
            crashlytics.log("Error in removeHighlight: $highlightId")
            crashlytics.setCustomKey("highlight_id", highlightId.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
    
    suspend fun clearHighlightsForHymn(hymnId: Long) {
        try {
            repository.clearHighlightsForHymn(hymnId)
        } catch (e: Exception) {
            crashlytics.log("Error in clearHighlightsForHymn: $hymnId")
            crashlytics.setCustomKey("hymn_id", hymnId.toInt())
            crashlytics.recordException(e)
            throw e
        }
    }
}

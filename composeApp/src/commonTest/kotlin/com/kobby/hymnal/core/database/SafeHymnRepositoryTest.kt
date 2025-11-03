package com.kobby.hymnal.core.database

import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.crashlytics.CrashlyticsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Tests for SafeHymnRepository exception handling and Crashlytics reporting
 */
class SafeHymnRepositoryTest {
    
    private class TestCrashlyticsManager : CrashlyticsManager {
        val exceptions = mutableListOf<Throwable>()
        val customKeys = mutableMapOf<String, Any>()
        val logs = mutableListOf<String>()
        
        override fun recordException(throwable: Throwable) {
            exceptions.add(throwable)
        }
        
        override fun setCustomKey(key: String, value: String) {
            customKeys[key] = value
        }
        
        override fun setCustomKey(key: String, value: Boolean) {
            customKeys[key] = value
        }
        
        override fun setCustomKey(key: String, value: Int) {
            customKeys[key] = value
        }
        
        override fun setUserId(userId: String) {}
        
        override fun log(message: String) {
            logs.add(message)
        }
        
        fun reset() {
            exceptions.clear()
            customKeys.clear()
            logs.clear()
        }
    }
    
    // Test double that mimics HymnRepository behavior without requiring actual database
    // Note: This is a simplified test implementation. In production code, consider using:
    // - A proper in-memory test database
    // - A mocking framework like MockK for better type safety
    // - Extracting an interface for easier testing
    private class TestHymnRepository(
        private val shouldThrow: Boolean = false,
        @Suppress("UNUSED_PARAMETER") database: Any? = null
    ) : HymnRepository(
        // Workaround: Cast to HymnDatabase for testing purposes
        // The methods are overridden, so the database is never actually used
        @Suppress("UNCHECKED_CAST")
        (object : Any() {} as com.kobby.hymnal.composeApp.database.HymnDatabase)
    ) {
        
        override fun getAllHymns(): Flow<List<Hymn>> {
            if (shouldThrow) throw RuntimeException("Test error")
            return flowOf(emptyList())
        }
        
        override suspend fun getHymnById(id: Long): Hymn? {
            if (shouldThrow) throw RuntimeException("Test error")
            return null
        }
        
        override suspend fun addToFavorites(hymnId: Long) {
            if (shouldThrow) throw RuntimeException("Test error")
        }
        
        override fun searchHymns(query: String): Flow<List<Hymn>> {
            if (shouldThrow) throw RuntimeException("Test error")
            return flowOf(emptyList())
        }
    }
    
    @Test
    fun testGetHymnByIdSuccess() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = false)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        val result = safeRepository.getHymnById(1L)
        
        assertNull(result)
        assertEquals(0, crashlytics.exceptions.size)
        assertEquals(0, crashlytics.logs.size)
    }
    
    @Test
    fun testGetHymnByIdFailure() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        val result = safeRepository.getHymnById(123L)
        
        assertNull(result)
        assertEquals(1, crashlytics.exceptions.size)
        assertEquals(1, crashlytics.logs.size)
        assertEquals("Error in getHymnById: 123", crashlytics.logs[0])
        assertEquals(123, crashlytics.customKeys["hymn_id"])
    }
    
    @Test
    fun testAddToFavoritesSuccess() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = false)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        safeRepository.addToFavorites(1L)
        
        assertEquals(0, crashlytics.exceptions.size)
    }
    
    @Test
    fun testAddToFavoritesFailure() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        try {
            safeRepository.addToFavorites(456L)
        } catch (e: RuntimeException) {
            // Expected to throw after logging
        }
        
        assertEquals(1, crashlytics.exceptions.size)
        assertEquals(1, crashlytics.logs.size)
        assertEquals("Error in addToFavorites: 456", crashlytics.logs[0])
        assertEquals(456, crashlytics.customKeys["hymn_id"])
    }
    
    @Test
    fun testSearchHymnsReportsExceptionOnError() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        try {
            safeRepository.searchHymns("test query").first()
        } catch (e: RuntimeException) {
            // Expected - exception is logged and re-thrown
        }
        
        assertEquals(1, crashlytics.exceptions.size)
        assertNotNull(crashlytics.logs.find { it.contains("Error in searchHymns") })
        assertEquals("test query", crashlytics.customKeys["search_query"])
    }
    
    @Test
    fun testGetAllHymnsSuccess() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = false)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        val result = safeRepository.getAllHymns().first()
        
        assertEquals(emptyList(), result)
        assertEquals(0, crashlytics.exceptions.size)
    }
    
    @Test
    fun testMultipleOperationsRecordsSeparateExceptions() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val repository = TestHymnRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        // First operation
        val result1 = safeRepository.getHymnById(1L)
        assertNull(result1)
        assertEquals(1, crashlytics.exceptions.size)
        
        // Second operation
        val result2 = safeRepository.getHymnById(2L)
        assertNull(result2)
        assertEquals(2, crashlytics.exceptions.size)
        
        // Verify custom keys were set for both
        assertEquals(2, crashlytics.customKeys["hymn_id"]) // Last value
    }
}

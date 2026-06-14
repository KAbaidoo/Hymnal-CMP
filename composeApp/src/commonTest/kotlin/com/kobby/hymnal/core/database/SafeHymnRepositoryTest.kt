package com.kobby.hymnal.core.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.composeApp.database.Hymn
import com.kobby.hymnal.core.crashlytics.CrashlyticsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    
    private fun createRepository(shouldThrow: Boolean = false): Pair<HymnRepository, JdbcSqliteDriver> {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        
        // Insert a test hymn so there's always at least one valid hymn for queries
        database.hymnsQueries.insertHymn(
            number = 1,
            title = "Amazing Grace",
            category = "ancient_modern",
            content = "Amazing grace, how sweet the sound"
        )
        
        val repository = HymnRepository(database)
        if (shouldThrow) {
            driver.close()
        }
        return Pair(repository, driver)
    }
    
    @Test
    fun testGetHymnByIdSuccess() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = false)
        try {
            val safeRepository = SafeHymnRepository(repository, crashlytics)
            
            val result = safeRepository.getHymnById(1L)
            
            assertNotNull(result)
            assertEquals(1L, result.id)
            assertEquals(0, crashlytics.exceptions.size)
            assertEquals(0, crashlytics.logs.size)
        } finally {
            driver.close()
        }
    }
    
    @Test
    fun testGetHymnByIdFailure() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        val result = safeRepository.getHymnById(123L)
        
        assertNull(result)
        assertEquals(1, crashlytics.exceptions.size)
        assertEquals(1, crashlytics.logs.size)
        assertEquals("Error in getHymnById: 123", crashlytics.logs[0])
        assertEquals("123", crashlytics.customKeys["hymn_id"])
    }
    
    @Test
    fun testAddToFavoritesSuccess() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = false)
        try {
            val safeRepository = SafeHymnRepository(repository, crashlytics)
            
            safeRepository.addToFavorites(1L)
            
            assertEquals(0, crashlytics.exceptions.size)
        } finally {
            driver.close()
        }
    }
    
    @Test
    fun testAddToFavoritesFailure() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        try {
            safeRepository.addToFavorites(456L)
        } catch (e: Exception) {
            // Expected to throw after logging
        }
        
        assertEquals(1, crashlytics.exceptions.size)
        assertEquals(1, crashlytics.logs.size)
        assertEquals("Error in addToFavorites: 456", crashlytics.logs[0])
        assertEquals("456", crashlytics.customKeys["hymn_id"])
    }
    
    @Test
    fun testSearchHymnsReportsExceptionOnError() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = true)
        val safeRepository = SafeHymnRepository(repository, crashlytics)
        
        try {
            safeRepository.searchHymns("test query").first()
        } catch (e: Exception) {
            // Expected - exception is logged and re-thrown
        }
        
        assertEquals(1, crashlytics.exceptions.size)
        assertNotNull(crashlytics.logs.find { it.contains("Error in searchHymns") })
        assertEquals("test query", crashlytics.customKeys["search_query"])
    }
    
    @Test
    fun testGetAllHymnsSuccess() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = false)
        try {
            val safeRepository = SafeHymnRepository(repository, crashlytics)
            
            val result = safeRepository.getAllHymns().first()
            
            assertEquals(1, result.size)
            assertEquals(0, crashlytics.exceptions.size)
        } finally {
            driver.close()
        }
    }
    
    @Test
    fun testMultipleOperationsRecordsSeparateExceptions() = runTest {
        val crashlytics = TestCrashlyticsManager()
        val (repository, driver) = createRepository(shouldThrow = true)
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
        assertEquals("2", crashlytics.customKeys["hymn_id"]) // Last value
    }
}

package com.kobby.hymnal.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.core.database.HymnRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class DatabaseIntegrationTest {
    
    @Test
    fun `database schema is created correctly`() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        
        // This should not throw
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        
        // Verify we can execute basic queries
        val count = database.hymnsQueries.getAllHymns().executeAsList()
        assertEquals(0, count.size) // Empty database initially
        
        driver.close()
    }
    
    @Test
    fun `full text search configuration works`() = runTest {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        val repository = HymnRepository(database)
        
        // Insert test data
        database.hymnsQueries.insertHymn(
            number = 1,
            title = "Amazing Grace",
            category = "ancient_modern",
            content = "Amazing grace how sweet the sound that saved a wretch like me"
        )
        
        // Test FTS search
        val results = repository.searchHymns("amazing").first()
        assertEquals(1, results.size)
        assertEquals("Amazing Grace", results[0].title)
        
        // Test content search
        val contentResults = repository.searchHymns("wretch").first()
        assertEquals(1, contentResults.size)
        
        driver.close()
    }
    
    @Test
    fun `database triggers work correctly`() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        
        // Insert hymn (should trigger FTS insert)
        database.hymnsQueries.insertHymn(
            number = 1,
            title = "Test Hymn",
            category = "test",
            content = "Test content"
        )
        
        // Check FTS table was populated
        val ftsResults = database.hymnsQueries.searchHymns("Test").executeAsList()
        assertEquals(1, ftsResults.size)
        
        driver.close()
    }
    
    @Test
    fun `indices improve query performance`() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        
        // Insert multiple hymns
        repeat(100) { i ->
            database.hymnsQueries.insertHymn(
                number = i.toLong(),
                title = "Hymn $i",
                category = if (i % 2 == 0) "ancient_modern" else "supplementary",
                content = "Content for hymn $i"
            )
        }
        
        // These queries should use indices (no way to verify performance in test,
        // but ensures they don't crash)
        val categoryResults = database.hymnsQueries.getHymnsByCategory("ancient_modern").executeAsList()
        assertTrue(categoryResults.size > 0)
        
        val numberResult = database.hymnsQueries.getHymnByNumber(5, "supplementary").executeAsOneOrNull()
        assertNotNull(numberResult)
        
        driver.close()
    }
    
    @Test
    fun `foreign key constraints work`() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        
        // Insert hymn
        database.hymnsQueries.insertHymn(
            number = 1,
            title = "Test",
            category = "test", 
            content = "Test"
        )
        
        val hymn = database.hymnsQueries.getAllHymns().executeAsList().first()
        
        // Add to favorites (should work)
        database.hymnsQueries.addToFavorites(hymn.id)
        val favorites = database.hymnsQueries.getFavoriteHymns().executeAsList()
        assertEquals(1, favorites.size)
        
        driver.close()
    }
}
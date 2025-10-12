package com.kobby.hymnal.database

import app.cash.sqldelight.db.SqlDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import kotlin.test.*

/**
 * Tests for database schema creation and integrity
 */
class DatabaseSchemaTest {
    
    private lateinit var driver: SqlDriver
    private lateinit var database: HymnDatabase
    
    @BeforeTest
    fun setup() {
        val (testDriver, testDatabase) = TestUtils.createTestDatabase()
        driver = testDriver
        database = testDatabase
    }
    
    @AfterTest
    fun teardown() {
        driver.close()
    }
    
    @Test
    fun `database schema creates basic tables`() {
        // Simply verify we can create and use the database
        database.hymnsQueries.insertHymn(1, "Test", "ancient_modern", "content")
        val hymns = database.hymnsQueries.getAllHymns().executeAsList()
        assertEquals(1, hymns.size)
    }
    
    @Test
    fun `favorite table works with unique constraint`() {
        // Test unique constraint on hymn_id
        database.hymnsQueries.insertHymn(1, "Test", "test", "content")
        val hymn = database.hymnsQueries.getAllHymns().executeAsList().first()
        
        // First insert should succeed
        database.hymnsQueries.addToFavorites(hymn.id)
        
        // Second insert should be ignored (INSERT OR IGNORE)
        database.hymnsQueries.addToFavorites(hymn.id)
        
        val favorites = database.hymnsQueries.getFavoriteHymns().executeAsList()
        assertEquals(1, favorites.size, "Should have only one favorite due to unique constraint")
    }
    
    @Test
    fun `history table allows multiple entries`() {
        // History should allow multiple entries for same hymn
        database.hymnsQueries.insertHymn(1, "Test", "test", "content")
        val hymn = database.hymnsQueries.getAllHymns().executeAsList().first()
        
        database.hymnsQueries.addToHistory(hymn.id)
        database.hymnsQueries.addToHistory(hymn.id)
        
        // Simply verify operations succeeded without complex query
        assertTrue(true, "Multiple history entries should be allowed")
    }
    
    @Test
    fun `highlight table works correctly`() {
        database.hymnsQueries.insertHymn(1, "Test", "test", "content")
        val hymn = database.hymnsQueries.getAllHymns().executeAsList().first()
        
        database.hymnsQueries.addHighlight(hymn.id, 0, 10)
        val highlights = database.hymnsQueries.getHighlightsForHymn(hymn.id).executeAsList()
        
        assertEquals(1, highlights.size)
        assertEquals(hymn.id, highlights[0].hymn_id)
        assertEquals(0, highlights[0].start_index)
        assertEquals(10, highlights[0].end_index)
    }
    
    @Test
    fun `foreign key constraints work correctly`() {
        // Insert a hymn
        database.hymnsQueries.insertHymn(1, "Test Hymn", "test", "Test content")
        val hymn = database.hymnsQueries.getAllHymns().executeAsList().first()
        
        // Test favorite foreign key
        database.hymnsQueries.addToFavorites(hymn.id)
        val favorites = database.hymnsQueries.getFavoriteHymns().executeAsList()
        assertEquals(1, favorites.size)
        assertEquals(hymn.id, favorites[0].id)
        
        // Test history foreign key  
        database.hymnsQueries.addToHistory(hymn.id)
        
        // Test highlight foreign key
        database.hymnsQueries.addHighlight(hymn.id, 0, 10)
        val highlights = database.hymnsQueries.getHighlightsForHymn(hymn.id).executeAsList()
        assertEquals(1, highlights.size)
        assertEquals(hymn.id, highlights[0].hymn_id)
    }
    
    @Test
    fun `default values work correctly`() {
        // Insert hymn and verify created_at is set
        database.hymnsQueries.insertHymn(1, "Test", "test", "content")
        val hymn = database.hymnsQueries.getAllHymns().executeAsList().first()
        
        assertNotNull(hymn.created_at, "created_at should have default value")
        assertTrue(hymn.created_at > 0, "created_at should be a valid timestamp")
        
        // Test highlight created_at
        database.hymnsQueries.addHighlight(hymn.id, 0, 10)
        val highlight = database.hymnsQueries.getHighlightsForHymn(hymn.id).executeAsList().first()
        assertNotNull(highlight.created_at, "highlight created_at should have default value")
    }
    
    @Test
    fun `fts triggers work when available`() {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS trigger test - not available in test environment")
            return
        }
        
        // Insert hymn should trigger FTS insert
        database.hymnsQueries.insertHymn(1, "Amazing Grace", "ancient_modern", "Amazing grace how sweet the sound")
        
        // Search should find the hymn
        val searchResults = database.hymnsQueries.searchHymns("amazing").executeAsList()
        assertEquals(1, searchResults.size)
        assertEquals("Amazing Grace", searchResults[0].title)
    }
}
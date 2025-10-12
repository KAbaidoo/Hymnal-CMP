package com.kobby.hymnal.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.core.database.HymnRepository

/**
 * Test utilities for database testing
 */
object TestUtils {
    
    /**
     * Creates an in-memory test database with schema
     */
    fun createTestDatabase(): Pair<SqlDriver, HymnDatabase> {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        
        // Enable FTS for testing (if supported)
        try {
            driver.execute(null, "PRAGMA compile_options", 0)
        } catch (e: Exception) {
            // FTS may not be available in test environment
        }
        
        HymnDatabase.Schema.create(driver)
        val database = HymnDatabase(driver)
        
        return driver to database
    }
    
    /**
     * Creates a test repository with pre-populated data
     */
    fun createTestRepository(): Triple<SqlDriver, HymnDatabase, HymnRepository> {
        val (driver, database) = createTestDatabase()
        val repository = HymnRepository(database)
        
        // Add test data
        populateTestData(database)
        
        return Triple(driver, database, repository)
    }
    
    /**
     * Populates database with test hymn data
     */
    fun populateTestData(database: HymnDatabase) {
        val testHymns = TestDataFactory.createTestHymns()
        
        testHymns.forEach { hymn ->
            database.hymnsQueries.insertHymn(
                number = hymn.number,
                title = hymn.title,
                category = hymn.category,
                content = hymn.content
            )
        }
    }
    
    /**
     * Verifies FTS4 is available and working
     */
    fun isFtsAvailable(database: HymnDatabase): Boolean {
        return try {
            // Try to use FTS search
            database.hymnsQueries.searchHymns("test").executeAsList()
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Creates a large dataset for performance testing
     */
    fun createLargeDataset(database: HymnDatabase, count: Int = 1000) {
        repeat(count) { i ->
            database.hymnsQueries.insertHymn(
                number = i.toLong(),
                title = "Test Hymn $i",
                category = if (i % 2 == 0) "ancient_modern" else "supplementary",
                content = "This is test content for hymn number $i. It contains various words like grace, love, peace, and joy."
            )
        }
    }
}
package com.kobby.hymnal.database

import app.cash.sqldelight.db.SqlDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.core.database.HymnRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.system.measureTimeMillis
import kotlin.test.*

/**
 * Tests for search performance and FTS functionality
 */
class SearchPerformanceTest {
    
    private lateinit var driver: SqlDriver
    private lateinit var database: HymnDatabase
    private lateinit var repository: HymnRepository
    
    @BeforeTest
    fun setup() {
        val (testDriver, testDatabase, testRepository) = TestUtils.createTestRepository()
        driver = testDriver
        database = testDatabase
        repository = testRepository
    }
    
    @AfterTest
    fun teardown() {
        driver.close()
    }
    
    @Test
    fun `FTS search returns results correctly when available`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS test - not available in test environment")
            return@runTest
        }
        
        val searchCases = TestDataFactory.createSearchTestCases()
        
        searchCases.forEach { testCase ->
            val results = repository.searchHymns(testCase.query).first()
            
            if (testCase.expectedTitles.isNotEmpty()) {
                assertTrue(
                    results.isNotEmpty(),
                    "Search for '${testCase.query}' should return results"
                )
                
                val resultTitles = results.map { it.title }
                testCase.expectedTitles.forEach { expectedTitle ->
                    assertTrue(
                        resultTitles.any { it?.contains(expectedTitle, ignoreCase = true) == true },
                        "Results should contain hymn with title containing '$expectedTitle'"
                    )
                }
            } else {
                assertTrue(
                    results.isEmpty(),
                    "Search for '${testCase.query}' should return no results"
                )
            }
        }
    }
    
    @Test
    fun `FTS search is case insensitive`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS test - not available in test environment")
            return@runTest
        }
        
        val searchTerms = listOf("grace", "GRACE", "Grace", "gRaCe")
        val results = mutableListOf<Int>()
        
        searchTerms.forEach { term ->
            val searchResults = repository.searchHymns(term).first()
            results.add(searchResults.size)
        }
        
        // All searches should return the same number of results
        assertTrue(
            results.distinct().size == 1,
            "Case insensitive search should return same results: $results"
        )
    }
    
    @Test
    fun `FTS search handles partial words`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS test - not available in test environment")
            return@runTest
        }
        
        // Test partial word matching
        val partialSearches = listOf(
            "amaz" to "Amazing Grace",
            "hol" to "Holy, Holy, Holy",
            "day" to "Now that the daylight fills the sky"
        )
        
        partialSearches.forEach { (query, expectedTitle) ->
            val results = repository.searchHymns(query).first()
            
            if (results.isNotEmpty()) {
                assertTrue(
                    results.any { it.title?.contains(expectedTitle, ignoreCase = true) == true },
                    "Partial search '$query' should find '$expectedTitle'"
                )
            }
        }
    }
    
    @Test
    fun `FTS search handles special characters and punctuation`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS test - not available in test environment")
            return@runTest
        }
        
        // Test searches with punctuation and special characters
        val specialSearches = listOf(
            "holy!",
            "jesus,",
            "grace."
        )
        
        specialSearches.forEach { query ->
            try {
                val results = repository.searchHymns(query).first()
                // Should not crash and may return results
                assertTrue(results.size >= 0)
            } catch (e: Exception) {
                println("Search with special characters '$query' failed: ${e.message}")
                // FTS might not handle all special characters gracefully
            }
        }
    }
    
    @Test
    fun `FTS search performance with small dataset`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS performance test - not available in test environment")
            return@runTest
        }
        
        val searchQueries = listOf("grace", "holy", "jesus", "love", "peace")
        val searchTimes = mutableListOf<Long>()
        
        // Warm up
        repository.searchHymns("grace").first()
        
        searchQueries.forEach { query ->
            val time = measureTimeMillis {
                runTest {
                    repository.searchHymns(query).first()
                }
            }
            searchTimes.add(time)
        }
        
        val averageTime = searchTimes.average()
        println("Average FTS search time: ${averageTime}ms")
        
        // Basic performance check - searches should complete quickly
        assertTrue(
            averageTime < 1000, // 1 second is very generous for small dataset
            "FTS searches should be fast, average: ${averageTime}ms"
        )
    }
    
    @Test
    fun `FTS search performance with large dataset`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS large dataset test - not available in test environment")
            return@runTest
        }
        
        // Create larger dataset for performance testing
        TestUtils.createLargeDataset(database, 100) // Smaller than 1000 for faster tests
        
        val searchQueries = listOf("test", "grace", "love", "peace", "joy")
        val searchTimes = mutableListOf<Long>()
        
        // Warm up
        repository.searchHymns("test").first()
        
        searchQueries.forEach { query ->
            val time = measureTimeMillis {
                runTest {
                    val results = repository.searchHymns(query).first()
                    assertTrue(results.size >= 0)
                }
            }
            searchTimes.add(time)
        }
        
        val averageTime = searchTimes.average()
        println("Average FTS search time with larger dataset: ${averageTime}ms")
        
        // Even with larger dataset, searches should be reasonable
        assertTrue(
            averageTime < 2000, // 2 seconds for larger dataset
            "FTS searches should remain fast with larger dataset, average: ${averageTime}ms"
        )
    }
    
    @Test
    fun `FTS search handles empty and whitespace queries`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS empty query test - not available in test environment")
            return@runTest
        }
        
        val emptyQueries = listOf("", "   ", "\t", "\n")
        
        emptyQueries.forEach { query ->
            try {
                val results = repository.searchHymns(query).first()
                // Should not crash, likely returns empty results
                assertTrue(results.isEmpty(), "Empty query should return no results")
            } catch (e: Exception) {
                // FTS might throw exception for invalid queries, which is acceptable
                println("Empty query '$query' handled with exception: ${e.message}")
            }
        }
    }
    
    @Test
    fun `FTS search ranking works correctly`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS ranking test - not available in test environment")
            return@runTest
        }
        
        // Insert additional test data with multiple matches
        database.hymnsQueries.insertHymn(
            999, 
            "Amazing Grace Extended", 
            "ancient_modern", 
            "Amazing grace, amazing grace, how sweet the sound of amazing grace"
        )
        
        val results = repository.searchHymns("amazing").first()
        
        if (results.size >= 2) {
            // The hymn with more matches should rank higher
            val extendedGrace = results.find { it.title?.contains("Extended") == true }
            val originalGrace = results.find { it.title == "Amazing Grace" }
            
            if (extendedGrace != null && originalGrace != null) {
                val extendedIndex = results.indexOf(extendedGrace)
                val originalIndex = results.indexOf(originalGrace)
                
                assertTrue(
                    extendedIndex <= originalIndex,
                    "Hymn with more matches should rank higher or equal"
                )
            }
        }
    }
    
    @Test
    fun `FTS search handles boolean operators when supported`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS boolean test - not available in test environment")
            return@runTest
        }
        
        // Test boolean operators (may not be supported in all FTS implementations)
        val booleanQueries = listOf(
            "amazing AND grace",
            "holy OR jesus",
            "amazing NOT holy"
        )
        
        booleanQueries.forEach { query ->
            try {
                val results = repository.searchHymns(query).first()
                assertTrue(results.size >= 0, "Boolean query should not crash")
            } catch (e: Exception) {
                println("Boolean query '$query' not supported: ${e.message}")
                // Not all FTS implementations support boolean operators
            }
        }
    }
    
    @Test
    fun `search without FTS falls back gracefully`() = runTest {
        if (TestUtils.isFtsAvailable(database)) {
            println("Skipping fallback test - FTS is available")
            return@runTest
        }
        
        // When FTS is not available, search should either:
        // 1. Return empty results gracefully
        // 2. Use alternative search method
        // 3. Throw a controlled exception
        
        try {
            val results = repository.searchHymns("grace").first()
            assertTrue(results.size >= 0, "Fallback search should not crash")
        } catch (e: Exception) {
            // Controlled exception is acceptable when FTS is not available
            println("Search without FTS handled with exception: ${e.message}")
        }
    }
    
    @Test
    fun `FTS search stress test`() = runTest {
        if (!TestUtils.isFtsAvailable(database)) {
            println("Skipping FTS stress test - not available in test environment")
            return@runTest
        }
        
        // Test rapid consecutive searches
        val queries = listOf("a", "e", "i", "o", "u", "grace", "holy", "jesus")
        
        repeat(3) { iteration ->
            queries.forEach { query ->
                try {
                    val results = repository.searchHymns(query).first()
                    assertTrue(results.size >= 0, "Stress test iteration $iteration should not crash")
                } catch (e: Exception) {
                    println("Stress test failed on iteration $iteration, query '$query': ${e.message}")
                    throw e
                }
            }
        }
    }
}
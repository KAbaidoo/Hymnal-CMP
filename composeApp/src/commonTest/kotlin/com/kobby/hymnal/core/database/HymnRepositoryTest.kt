package com.kobby.hymnal.core.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kobby.hymnal.composeApp.database.HymnDatabase
import com.kobby.hymnal.composeApp.database.Hymn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HymnRepositoryTest {

    private fun createInMemoryDatabase(): HymnDatabase {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        HymnDatabase.Schema.create(driver)
        return HymnDatabase(driver)
    }

    private fun insertTestHymns(database: HymnDatabase) {
        database.hymnsQueries.insertHymn(
            number = 1,
            title = "Amazing Grace",
            category = HymnRepository.CATEGORY_ANCIENT_MODERN,
            content = "Amazing grace, how sweet the sound"
        )
        database.hymnsQueries.insertHymn(
            number = 2,
            title = "Be Thou My Vision",
            category = HymnRepository.CATEGORY_ANCIENT_MODERN,
            content = "Be thou my vision, O Lord of my heart"
        )
        database.hymnsQueries.insertHymn(
            number = 1,
            title = "Holy Holy Holy",
            category = HymnRepository.CATEGORY_SUPPLEMENTARY,
            content = "Holy, holy, holy! Lord God Almighty"
        )
    }

    @Test
    fun `getAllHymns returns all hymns ordered by category and number`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)

        // When
        val hymns = repository.getAllHymns().first()

        // Then
        assertEquals(3, hymns.size)
        assertEquals("Amazing Grace", hymns[0].title)
        assertEquals("Be Thou My Vision", hymns[1].title)
        assertEquals("Holy Holy Holy", hymns[2].title)
    }

    @Test
    fun `getHymnsByCategory returns only hymns from specified category`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)

        // When
        val hymns = repository.getHymnsByCategory(HymnRepository.CATEGORY_ANCIENT_MODERN).first()

        // Then
        assertEquals(2, hymns.size)
        assertEquals("Amazing Grace", hymns[0].title)
        assertEquals("Be Thou My Vision", hymns[1].title)
    }

    @Test
    fun `getHymnById returns correct hymn when id exists`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id

        // When
        val hymn = repository.getHymnById(firstHymnId)

        // Then
        assertNotNull(hymn)
        assertEquals(firstHymnId, hymn.id)
        assertEquals("Amazing Grace", hymn.title)
    }

    @Test
    fun `getHymnById returns null when id does not exist`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        val repository = HymnRepository(database)

        // When
        val hymn = repository.getHymnById(999)

        // Then
        assertNull(hymn)
    }

    @Test
    fun `getHymnByNumber returns correct hymn when number and category exist`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)

        // When
        val hymn = repository.getHymnByNumber(1, HymnRepository.CATEGORY_ANCIENT_MODERN)

        // Then
        assertNotNull(hymn)
        assertEquals(1L, hymn.number)
        assertEquals("Amazing Grace", hymn.title)
        assertEquals(HymnRepository.CATEGORY_ANCIENT_MODERN, hymn.category)
    }

    @Test
    fun `getHymnByNumber returns null when number does not exist in category`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)

        // When
        val hymn = repository.getHymnByNumber(999, HymnRepository.CATEGORY_ANCIENT_MODERN)

        // Then
        assertNull(hymn)
    }

    @Test
    fun `getRandomHymn returns a hymn when hymns exist`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)

        // When
        val hymn = repository.getRandomHymn()

        // Then
        assertNotNull(hymn)
        assertTrue(hymn.title in listOf("Amazing Grace", "Be Thou My Vision", "Holy Holy Holy"))
    }

    @Test
    fun `getRandomHymn returns null when no hymns exist`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        val repository = HymnRepository(database)

        // When
        val hymn = repository.getRandomHymn()

        // Then
        assertNull(hymn)
    }

    @Test
    fun `searchHymns returns hymns matching search query`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)

        // When
        val hymns = repository.searchHymns("grace").first()

        // Then
        assertEquals(1, hymns.size)
        assertEquals("Amazing Grace", hymns[0].title)
    }

    @Test
    fun `addToFavorites and getFavoriteHymns work correctly`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id

        // When
        repository.addToFavorites(firstHymnId)
        val favorites = repository.getFavoriteHymns().first()

        // Then
        assertEquals(1, favorites.size)
        assertEquals(firstHymnId, favorites[0].id)
    }

    @Test
    fun `removeFromFavorites removes hymn from favorites`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        repository.addToFavorites(firstHymnId)

        // When
        repository.removeFromFavorites(firstHymnId)
        val favorites = repository.getFavoriteHymns().first()

        // Then
        assertEquals(0, favorites.size)
    }

    @Test
    fun `isFavorite returns true when hymn is favorited`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        repository.addToFavorites(firstHymnId)

        // When
        val isFavorite = repository.isFavorite(firstHymnId)

        // Then
        assertTrue(isFavorite)
    }

    @Test
    fun `isFavorite returns false when hymn is not favorited`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id

        // When
        val isFavorite = repository.isFavorite(firstHymnId)

        // Then
        assertFalse(isFavorite)
    }

    @Test
    fun `addToHistory and getRecentHymns work correctly`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        val secondHymnId = allHymns[1].id

        // When
        repository.addToHistory(firstHymnId)
        repository.addToHistory(secondHymnId)
        val recentHymns = repository.getRecentHymns(10).first()

        // Then
        assertEquals(2, recentHymns.size)
        // First added should be first in results
        assertEquals(firstHymnId, recentHymns[0].id)
        assertEquals(secondHymnId, recentHymns[1].id)
    }

    @Test
    fun `getRecentHymns respects limit parameter`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        
        allHymns.forEach { hymn ->
            repository.addToHistory(hymn.id)
        }

        // When
        val recentHymns = repository.getRecentHymns(2).first()

        // Then
        assertEquals(2, recentHymns.size)
    }

    @Test
    fun `clearHistory removes all history entries`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        repository.addToHistory(allHymns[0].id)
        repository.addToHistory(allHymns[1].id)

        // When
        repository.clearHistory()
        val recentHymns = repository.getRecentHymns(10).first()

        // Then
        assertEquals(0, recentHymns.size)
    }

    @Test
    fun `addHighlight and getHighlightsForHymn work correctly`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id

        // When
        repository.addHighlight(firstHymnId, 0, 10, 1)
        val highlights = repository.getHighlightsForHymn(firstHymnId)

        // Then
        assertEquals(1, highlights.size)
        assertEquals(firstHymnId, highlights[0].hymn_id)
        assertEquals(0L, highlights[0].start_index)
        assertEquals(10L, highlights[0].end_index)
        assertEquals(1L, highlights[0].color_index)
    }

    @Test
    fun `getHymnsWithHighlights returns only hymns with highlights`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        repository.addHighlight(firstHymnId, 0, 10, 0)

        // When
        val hymnsWithHighlights = repository.getHymnsWithHighlights().first()

        // Then
        assertEquals(1, hymnsWithHighlights.size)
        assertEquals(firstHymnId, hymnsWithHighlights[0].id)
    }

    @Test
    fun `updateHighlightColor updates the color of highlight`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        repository.addHighlight(firstHymnId, 0, 10, 0)
        val highlights = repository.getHighlightsForHymn(firstHymnId)
        val highlightId = highlights[0].id

        // When
        repository.updateHighlightColor(highlightId, 2)
        val updatedHighlights = repository.getHighlightsForHymn(firstHymnId)

        // Then
        assertEquals(1, updatedHighlights.size)
        assertEquals(2L, updatedHighlights[0].color_index)
    }

    @Test
    fun `removeHighlight removes specific highlight`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        repository.addHighlight(firstHymnId, 0, 10, 0)
        repository.addHighlight(firstHymnId, 20, 30, 1)
        val highlights = repository.getHighlightsForHymn(firstHymnId)
        val firstHighlightId = highlights[0].id

        // When
        repository.removeHighlight(firstHighlightId)
        val remainingHighlights = repository.getHighlightsForHymn(firstHymnId)

        // Then
        assertEquals(1, remainingHighlights.size)
        assertEquals(20L, remainingHighlights[0].start_index)
    }

    @Test
    fun `clearHighlightsForHymn removes all highlights for specific hymn`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id
        val secondHymnId = allHymns[1].id
        repository.addHighlight(firstHymnId, 0, 10, 0)
        repository.addHighlight(firstHymnId, 20, 30, 1)
        repository.addHighlight(secondHymnId, 0, 5, 0)

        // When
        repository.clearHighlightsForHymn(firstHymnId)
        val firstHymnHighlights = repository.getHighlightsForHymn(firstHymnId)
        val secondHymnHighlights = repository.getHighlightsForHymn(secondHymnId)

        // Then
        assertEquals(0, firstHymnHighlights.size)
        assertEquals(1, secondHymnHighlights.size)
    }

    @Test
    fun `addToFavorites is idempotent - adding same hymn twice does not create duplicates`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()
        val firstHymnId = allHymns[0].id

        // When
        repository.addToFavorites(firstHymnId)
        repository.addToFavorites(firstHymnId)
        val favorites = repository.getFavoriteHymns().first()

        // Then
        assertEquals(1, favorites.size)
    }

    @Test
    fun `multiple hymns can be in history and ordered by most recent access`() = runTest {
        // Given
        val database = createInMemoryDatabase()
        insertTestHymns(database)
        val repository = HymnRepository(database)
        val allHymns = database.hymnsQueries.getAllHymns().executeAsList()

        // When - Add hymns in specific order
        repository.addToHistory(allHymns[0].id)
        repository.addToHistory(allHymns[1].id)
        repository.addToHistory(allHymns[2].id)
        repository.addToHistory(allHymns[0].id) // Access first hymn again

        val recentHymns = repository.getRecentHymns(10).first()

        // Then - First hymn should be most recent
        assertEquals(3, recentHymns.size)
        assertEquals(allHymns[0].id, recentHymns[0].id)
    }
}

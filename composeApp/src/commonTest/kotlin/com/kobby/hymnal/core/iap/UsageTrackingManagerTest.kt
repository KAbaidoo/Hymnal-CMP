package com.kobby.hymnal.core.iap

import com.russhwolf.settings.MapSettings
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsageTrackingManagerTest {

    private fun createManager(): UsageTrackingManager {
        val settings = MapSettings()
        val storage = PurchaseStorage(settings)
        return UsageTrackingManager(storage)
    }

    private fun createManagerWithStorage(): Pair<UsageTrackingManager, PurchaseStorage> {
        val settings = MapSettings()
        val storage = PurchaseStorage(settings)
        return Pair(UsageTrackingManager(storage), storage)
    }

    @Test
    fun `initial state should not show review prompt`() {
        val manager = createManager()
        assertFalse(manager.shouldShowReviewPrompt())
    }

    @Test
    fun `review prompt triggers after 5 reads`() {
        val (manager, storage) = createManagerWithStorage()

        // Read 4 times
        repeat(4) {
            manager.recordHymnRead(isSupporter = false)
        }
        assertFalse(manager.shouldShowReviewPrompt(), "Should not show after 4 reads")

        // Read 5th time
        manager.recordHymnRead(isSupporter = false)
        assertTrue(manager.shouldShowReviewPrompt(), "Should show after 5 reads")
        
        // Check storage directly
        assertEquals(5, storage.reviewHymnsReadCount)
    }

    @Test
    fun `review prompt triggers after 3 searches`() {
        val (manager, storage) = createManagerWithStorage()

        // Search 2 times
        repeat(2) {
            manager.recordSearchSuccess()
        }
        assertFalse(manager.shouldShowReviewPrompt(), "Should not show after 2 searches")

        // Search 3rd time
        manager.recordSearchSuccess()
        assertTrue(manager.shouldShowReviewPrompt(), "Should show after 3 searches")
        
        // Check storage directly
        assertEquals(3, storage.reviewSearchCount)
    }

    @Test
    fun `showing prompt resets counters and updates timestamp`() {
        val (manager, storage) = createManagerWithStorage()

        // Trigger condition
        repeat(5) { manager.recordHymnRead(false) }
        assertTrue(manager.shouldShowReviewPrompt())

        // Record prompt shown
        val beforeTime = Clock.System.now().toEpochMilliseconds()
        manager.recordReviewPromptShown()
        val afterTime = Clock.System.now().toEpochMilliseconds()

        // Verify resets
        assertEquals(0, storage.reviewHymnsReadCount)
        assertEquals(0, storage.reviewSearchCount)
        
        // Verify timestamp updated
        assertTrue(storage.lastReviewPromptTimestamp >= beforeTime)
        assertTrue(storage.lastReviewPromptTimestamp <= afterTime)
        
        // Verify legacy flag set
        assertTrue(storage.hasShownReviewPrompt)
    }

    @Test
    fun `cooldown prevents prompt even if engagement met`() {
        val (manager, storage) = createManagerWithStorage()

        // 1. Trigger first prompt
        repeat(5) { manager.recordHymnRead(false) }
        manager.recordReviewPromptShown()
        
        // 2. Engage again (5 reads)
        repeat(5) { manager.recordHymnRead(false) }
        assertEquals(5, storage.reviewHymnsReadCount)
        
        // 3. Check prompt - Should be FALSE because cooldown is active (0 ms elapsed < 90 days)
        assertFalse(manager.shouldShowReviewPrompt(), "Should block prompt due to cooldown")
        
        // 4. Simulate time passing (91 days)
        // We can't easily mock Clock in the current implementation without dependency injection of a Clock provider.
        // However, we can manually manipulate the storage timestamp.
        val ninetyOneDaysMs = 91L * 24 * 60 * 60 * 1000
        storage.lastReviewPromptTimestamp = Clock.System.now().toEpochMilliseconds() - ninetyOneDaysMs
        
        // 5. Check prompt again - Should be TRUE now
        assertTrue(manager.shouldShowReviewPrompt(), "Should allow prompt after cooldown expires")
    }

    @Test
    fun `new counters are independent of legacy counters`() {
        val (manager, storage) = createManagerWithStorage()
        
        // Simulate existing user state
        storage.hymnsReadCount = 100
        storage.hasShownReviewPrompt = false // or true, doesn't matter for new logic
        
        // New counters start at 0
        assertEquals(0, storage.reviewHymnsReadCount)
        
        // User reads 1 hymn
        manager.recordHymnRead(false)
        
        assertEquals(101, storage.hymnsReadCount) // Legacy/Donation counter increments
        assertEquals(1, storage.reviewHymnsReadCount) // Review counter increments from 0
        
        assertFalse(manager.shouldShowReviewPrompt())
    }
}

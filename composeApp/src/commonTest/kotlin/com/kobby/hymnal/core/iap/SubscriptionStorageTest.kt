package com.kobby.hymnal.core.iap

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for PurchaseStorage in the freemium model.
 * Only tests one-time purchase functionality since subscriptions were removed.
 */
class SubscriptionStorageTest {

    private fun createTestStorage(): PurchaseStorage {
        return PurchaseStorage(MapSettings())
    }

    @Test
    fun `recordPurchase with ONE_TIME_PURCHASE sets correct type`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        storage.recordPurchase(
            productId = "support_basic",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE
        )

        // Then
        assertEquals(PurchaseType.ONE_TIME_PURCHASE, storage.purchaseType)
        assertEquals("support_basic", storage.productId)
        assertTrue(storage.isSubscribed)
    }

    @Test
    fun `getEntitlementState returns SUPPORTED for one-time purchase`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase(
            productId = "support_generous",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE
        )
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.SUPPORTED, state)
    }

    @Test
    fun `getEntitlementState returns NONE for fresh install`() = runTest {
        // Given
        val storage = createTestStorage()
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.NONE, state)
    }

    @Test
    fun `getEntitlementInfo returns complete information for purchase`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("support_basic", PurchaseType.ONE_TIME_PURCHASE)

        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertEquals(EntitlementState.SUPPORTED, info.state)
        assertTrue(info.hasSupported) // Changed from hasAccess
        assertEquals(PurchaseType.ONE_TIME_PURCHASE, info.purchaseType)
    }

    @Test
    fun `hasAccess is true for one-time purchase`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("support_basic", PurchaseType.ONE_TIME_PURCHASE)

        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertTrue(info.hasAccess)
    }

    @Test
    fun `hasAccess is false for no purchase`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertFalse(info.hasAccess)
    }

    @Test
    fun `clearAll removes all data`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("support_basic", PurchaseType.ONE_TIME_PURCHASE)

        // When
        storage.clearAll()
        
        // Then
        assertFalse(storage.isSubscribed)
        assertNull(storage.productId)
        assertEquals(PurchaseType.NONE, storage.purchaseType)
    }

    @Test
    fun `ONE_TIME_PURCHASE never expires`() = runTest {
        // Given
        val storage = createTestStorage()

        // When - record one-time purchase
        storage.recordPurchase(
            productId = "support_generous",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE
        )

        // Then - should always be SUPPORTED (never expires)
        assertEquals(EntitlementState.SUPPORTED, storage.getEntitlementState())
        assertTrue(storage.getEntitlementInfo().hasSupported)
    }

    @Test
    fun `lastVerificationTime is updated on recordPurchase`() = runTest {
        // Given
        val storage = createTestStorage()
        val beforeTime = Clock.System.now().toEpochMilliseconds()

        // When
        storage.recordPurchase("support_basic", PurchaseType.ONE_TIME_PURCHASE)

        // Then
        assertTrue(storage.lastVerificationTime >= beforeTime)
        assertTrue(storage.lastVerificationTime <= Clock.System.now().toEpochMilliseconds())
    }

    @Test
    fun `hasSupported is true after purchase`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("support_basic", PurchaseType.ONE_TIME_PURCHASE)

        // When
        val info = storage.getEntitlementInfo()

        // Then
        assertTrue(info.hasSupported)
    }

    @Test
    fun `hasSupported is false with no purchase`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        val info = storage.getEntitlementInfo()

        // Then
        assertFalse(info.hasSupported)
    }

    // Usage tracking tests

    @Test
    fun `hymnsReadCount starts at zero`() = runTest {
        // Given
        val storage = createTestStorage()

        // Then
        assertEquals(0, storage.hymnsReadCount)
    }

    @Test
    fun `hymnsReadCount can be incremented`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        storage.hymnsReadCount = 5

        // Then
        assertEquals(5, storage.hymnsReadCount)
    }

    // DEPRECATED TESTS - Feature access tracking removed in favor of donation prompts
    /*
    @Test
    fun `feature access attempts start at zero`() = runTest {
        // Given
        val storage = createTestStorage()

        // Then
        assertEquals(0, storage.getFeatureAccessAttempts(PremiumFeature.FAVORITES))
        assertEquals(0, storage.getFeatureAccessAttempts(PremiumFeature.HIGHLIGHTS))
        assertEquals(0, storage.getFeatureAccessAttempts(PremiumFeature.FONT_CUSTOMIZATION))
    }

    @Test
    fun `feature access attempts can be incremented`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        storage.setFeatureAccessAttempts(PremiumFeature.FAVORITES, 3)

        // Then
        assertEquals(3, storage.getFeatureAccessAttempts(PremiumFeature.FAVORITES))
    }

    @Test
    fun `getAllFeatureAccessAttempts returns all attempts`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.setFeatureAccessAttempts(PremiumFeature.FAVORITES, 2)
        storage.setFeatureAccessAttempts(PremiumFeature.HIGHLIGHTS, 5)

        // When
        val attempts = storage.getAllFeatureAccessAttempts()

        // Then
        assertEquals(2, attempts[PremiumFeature.FAVORITES])
        assertEquals(5, attempts[PremiumFeature.HIGHLIGHTS])
        assertEquals(0, attempts[PremiumFeature.FONT_CUSTOMIZATION])
    }
    */

    // TODO: Add new tests for donation tracking
    // - Test donationPromptCount starts at 0
    // - Test nextPromptThreshold defaults to 10
    // - Test recordDonation() resets counters
    // - Test shouldShowYearlyReminder() after 365 days
    // - Test calculateNextThreshold() exponential backoff
}

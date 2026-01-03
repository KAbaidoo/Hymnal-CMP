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

class SubscriptionStorageTest {

    private fun createTestStorage(): SubscriptionStorage {
        return SubscriptionStorage(MapSettings())
    }

    @Test
    fun `initializeFirstInstallIfNeeded sets first install date on first call`() = runTest {
        // Given
        val storage = createTestStorage()
        
        // When
        storage.initializeFirstInstallIfNeeded()
        
        // Then
        assertTrue(storage.firstInstallDate > 0)
    }

    @Test
    fun `initializeFirstInstallIfNeeded does not override existing date`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = 1000L
        
        // When
        storage.initializeFirstInstallIfNeeded()
        
        // Then
        assertEquals(1000L, storage.firstInstallDate)
    }

    @Test
    fun `getTrialDaysRemaining returns correct value for new install`() = runTest {
        // Given
        val storage = createTestStorage()
        val currentTime = Clock.System.now().toEpochMilliseconds()
        storage.firstInstallDate = currentTime
        
        // When
        val daysRemaining = storage.getTrialDaysRemaining()
        
        // Then
        assertNotNull(daysRemaining)
        assertEquals(30, daysRemaining)
    }

    @Test
    fun `getTrialDaysRemaining returns correct value for install 15 days ago`() = runTest {
        // Given
        val storage = createTestStorage()
        val fifteenDaysAgo = Clock.System.now().toEpochMilliseconds() - (15 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fifteenDaysAgo
        
        // When
        val daysRemaining = storage.getTrialDaysRemaining()
        
        // Then
        assertNotNull(daysRemaining)
        assertEquals(15, daysRemaining)
    }

    @Test
    fun `getTrialDaysRemaining returns 0 for expired trial`() = runTest {
        // Given
        val storage = createTestStorage()
        val fortyDaysAgo = Clock.System.now().toEpochMilliseconds() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fortyDaysAgo
        
        // When
        val daysRemaining = storage.getTrialDaysRemaining()
        
        // Then
        assertNotNull(daysRemaining)
        assertEquals(0, daysRemaining)
    }

    @Test
    fun `getTrialDaysRemaining returns null when subscribed`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = Clock.System.now().toEpochMilliseconds()
        storage.isSubscribed = true
        
        // When
        val daysRemaining = storage.getTrialDaysRemaining()
        
        // Then
        assertNull(daysRemaining)
    }

    @Test
    fun `isTrialActive returns true for active trial`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = Clock.System.now().toEpochMilliseconds()

        // When & Then
        assertTrue(storage.isTrialActive())
    }

    @Test
    fun `isTrialActive returns false for expired trial`() = runTest {
        // Given
        val storage = createTestStorage()
        val fortyDaysAgo = Clock.System.now().toEpochMilliseconds() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fortyDaysAgo
        
        // When & Then
        assertFalse(storage.isTrialActive())
    }

    @Test
    fun `recordPurchase sets all purchase fields correctly`() = runTest {
        // Given
        val storage = createTestStorage()
        val productId = "test_product"
        val purchaseTime = Clock.System.now().toEpochMilliseconds()

        // When
        storage.recordPurchase(
            productId = productId,
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION,
            purchaseTimestamp = purchaseTime
        )
        
        // Then
        assertTrue(storage.isSubscribed)
        assertEquals(productId, storage.productId)
        assertEquals(PurchaseType.YEARLY_SUBSCRIPTION, storage.purchaseType)
        assertEquals(purchaseTime, storage.purchaseDate)
    }

    @Test
    fun `clearSubscription resets subscription data`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("test", PurchaseType.ONE_TIME_PURCHASE)
        
        // When
        storage.clearSubscription()
        
        // Then
        assertFalse(storage.isSubscribed)
        assertNull(storage.productId)
        assertEquals(PurchaseType.NONE, storage.purchaseType)
        assertNull(storage.purchaseDate)
    }

    @Test
    fun `getEntitlementState returns TRIAL for new install`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = Clock.System.now().toEpochMilliseconds()

        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.TRIAL, state)
    }

    @Test
    fun `getEntitlementState returns TRIAL_EXPIRED for expired trial`() = runTest {
        // Given
        val storage = createTestStorage()
        val fortyDaysAgo = Clock.System.now().toEpochMilliseconds() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fortyDaysAgo
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.TRIAL_EXPIRED, state)
    }

    @Test
    fun `getEntitlementState returns SUBSCRIBED for active subscription`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("test", PurchaseType.YEARLY_SUBSCRIPTION)
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.SUBSCRIBED, state)
    }

    @Test
    fun `getEntitlementState returns SUBSCRIPTION_EXPIRED for expired subscription with expiration date`() = runTest {
        // Given
        val storage = createTestStorage()
        val yesterday = Clock.System.now().toEpochMilliseconds() - SubscriptionStorage.MILLIS_PER_DAY
        storage.recordPurchase(
            productId = "test",
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION,
            expirationTimestamp = yesterday
        )
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.SUBSCRIPTION_EXPIRED, state)
    }

    @Test
    fun `getEntitlementState returns SUBSCRIBED for one-time purchase even with past expiration date`() = runTest {
        // Given
        val storage = createTestStorage()
        val yesterday = Clock.System.now().toEpochMilliseconds() - SubscriptionStorage.MILLIS_PER_DAY
        storage.recordPurchase(
            productId = "test_onetime",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE,
            expirationTimestamp = yesterday  // Should be ignored for one-time purchases
        )
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.SUBSCRIBED, state)
    }

    @Test
    fun `getEntitlementState returns SUBSCRIBED for one-time purchase without expiration date`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase(
            productId = "test_onetime",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE
        )
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.SUBSCRIBED, state)
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
    fun `getEntitlementInfo returns complete information`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = Clock.System.now().toEpochMilliseconds()

        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertEquals(EntitlementState.TRIAL, info.state)
        assertTrue(info.hasAccess)
        assertTrue(info.isInTrial)
        assertFalse(info.needsPaywall)
        assertEquals(30, info.trialDaysRemaining)
    }

    @Test
    fun `hasAccess is true for trial`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = Clock.System.now().toEpochMilliseconds()

        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertTrue(info.hasAccess)
    }

    @Test
    fun `hasAccess is true for subscribed`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("test", PurchaseType.ONE_TIME_PURCHASE)
        
        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertTrue(info.hasAccess)
    }

    @Test
    fun `hasAccess is false for expired trial`() = runTest {
        // Given
        val storage = createTestStorage()
        val fortyDaysAgo = Clock.System.now().toEpochMilliseconds() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fortyDaysAgo
        
        // When
        val info = storage.getEntitlementInfo()
        
        // Then
        assertFalse(info.hasAccess)
        assertTrue(info.needsPaywall)
    }

    @Test
    fun `clearAll removes all data`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.firstInstallDate = Clock.System.now().toEpochMilliseconds()
        storage.recordPurchase("test", PurchaseType.YEARLY_SUBSCRIPTION)
        
        // When
        storage.clearAll()
        
        // Then
        assertEquals(0L, storage.firstInstallDate)
        assertFalse(storage.isSubscribed)
        assertNull(storage.productId)
        assertEquals(PurchaseType.NONE, storage.purchaseType)
    }

    @Test
    fun `recordPurchase with ONE_TIME_PURCHASE sets correct type`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        storage.recordPurchase(
            productId = "onetime_purchase",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE
        )

        // Then
        assertEquals(PurchaseType.ONE_TIME_PURCHASE, storage.purchaseType)
        assertEquals("onetime_purchase", storage.productId)
        assertTrue(storage.isSubscribed)
    }

    @Test
    fun `recordPurchase with YEARLY_SUBSCRIPTION sets correct type`() = runTest {
        // Given
        val storage = createTestStorage()

        // When
        storage.recordPurchase(
            productId = "yearly_subscription",
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION
        )

        // Then
        assertEquals(PurchaseType.YEARLY_SUBSCRIPTION, storage.purchaseType)
        assertEquals("yearly_subscription", storage.productId)
        assertTrue(storage.isSubscribed)
    }

    @Test
    fun `ONE_TIME_PURCHASE ignores expiration date completely`() = runTest {
        // Given
        val storage = createTestStorage()
        val yearAgo = Clock.System.now().toEpochMilliseconds() - (365 * SubscriptionStorage.MILLIS_PER_DAY)

        // When - record one-time purchase with very old expiration date
        storage.recordPurchase(
            productId = "onetime_purchase",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE,
            expirationTimestamp = yearAgo
        )

        // Then - should still be SUBSCRIBED
        assertEquals(EntitlementState.SUBSCRIBED, storage.getEntitlementState())
        assertTrue(storage.getEntitlementInfo().hasAccess)
    }

    @Test
    fun `YEARLY_SUBSCRIPTION with future expiration is SUBSCRIBED`() = runTest {
        // Given
        val storage = createTestStorage()
        val nextYear = Clock.System.now().toEpochMilliseconds() + (365 * SubscriptionStorage.MILLIS_PER_DAY)

        // When
        storage.recordPurchase(
            productId = "yearly_subscription",
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION,
            expirationTimestamp = nextYear
        )

        // Then
        assertEquals(EntitlementState.SUBSCRIBED, storage.getEntitlementState())
        assertTrue(storage.getEntitlementInfo().hasAccess)
    }

    @Test
    fun `YEARLY_SUBSCRIPTION without expiration date is SUBSCRIBED`() = runTest {
        // Given
        val storage = createTestStorage()

        // When - record subscription without expiration date
        storage.recordPurchase(
            productId = "yearly_subscription",
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION,
            expirationTimestamp = null
        )

        // Then - should still be SUBSCRIBED (platform manages expiration)
        assertEquals(EntitlementState.SUBSCRIBED, storage.getEntitlementState())
        assertTrue(storage.getEntitlementInfo().hasAccess)
    }

    @Test
    fun `lastVerificationTime is updated on recordPurchase`() = runTest {
        // Given
        val storage = createTestStorage()
        val beforeTime = Clock.System.now().toEpochMilliseconds()

        // When
        storage.recordPurchase("test", PurchaseType.ONE_TIME_PURCHASE)

        // Then
        assertTrue(storage.lastVerificationTime >= beforeTime)
        assertTrue(storage.lastVerificationTime <= Clock.System.now().toEpochMilliseconds())
    }

    @Test
    fun `getTrialDaysRemaining accounts for partial days correctly`() = runTest {
        // Given
        val storage = createTestStorage()
        // Set to 14.5 days ago
        val timeAgo = Clock.System.now().toEpochMilliseconds() - (14 * SubscriptionStorage.MILLIS_PER_DAY + SubscriptionStorage.MILLIS_PER_DAY / 2)
        storage.firstInstallDate = timeAgo

        // When
        val daysRemaining = storage.getTrialDaysRemaining()

        // Then - should round down to 15 days remaining
        assertNotNull(daysRemaining)
        assertEquals(15, daysRemaining)
    }

    @Test
    fun `switching from YEARLY_SUBSCRIPTION to ONE_TIME_PURCHASE works correctly`() = runTest {
        // Given
        val storage = createTestStorage()
        val yesterday = Clock.System.now().toEpochMilliseconds() - SubscriptionStorage.MILLIS_PER_DAY

        // First purchase yearly subscription that's about to expire
        storage.recordPurchase(
            productId = "yearly_subscription",
            purchaseType = PurchaseType.YEARLY_SUBSCRIPTION,
            expirationTimestamp = yesterday
        )

        // Verify it's expired
        assertEquals(EntitlementState.SUBSCRIPTION_EXPIRED, storage.getEntitlementState())

        // When - user purchases one-time
        storage.recordPurchase(
            productId = "onetime_purchase",
            purchaseType = PurchaseType.ONE_TIME_PURCHASE
        )

        // Then - should be SUBSCRIBED with no expiration concerns
        assertEquals(EntitlementState.SUBSCRIBED, storage.getEntitlementState())
        assertEquals(PurchaseType.ONE_TIME_PURCHASE, storage.purchaseType)
    }

    @Test
    fun `EntitlementInfo hasAccess is true for ONE_TIME_PURCHASE`() = runTest {
        // Given
        val storage = createTestStorage()
        storage.recordPurchase("onetime_purchase", PurchaseType.ONE_TIME_PURCHASE)

        // When
        val info = storage.getEntitlementInfo()

        // Then
        assertTrue(info.hasAccess)
        assertFalse(info.isInTrial)
        assertFalse(info.needsPaywall)
        assertEquals(EntitlementState.SUBSCRIBED, info.state)
    }

    @Test
    fun `product ID constants are correct for both platforms`() = runTest {
        // This test documents the expected product IDs
        val yearlyId = "yearly_subscription"
        val onetimeId = "onetime_purchase"

        val storage = createTestStorage()

        // Test yearly subscription
        storage.recordPurchase(yearlyId, PurchaseType.YEARLY_SUBSCRIPTION)
        assertEquals(yearlyId, storage.productId)

        // Test one-time purchase
        storage.recordPurchase(onetimeId, PurchaseType.ONE_TIME_PURCHASE)
        assertEquals(onetimeId, storage.productId)
    }
}

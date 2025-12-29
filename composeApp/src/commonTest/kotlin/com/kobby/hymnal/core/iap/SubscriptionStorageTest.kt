package com.kobby.hymnal.core.iap

import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
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
        val currentTime = System.currentTimeMillis()
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
        val fifteenDaysAgo = System.currentTimeMillis() - (15 * SubscriptionStorage.MILLIS_PER_DAY)
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
        val fortyDaysAgo = System.currentTimeMillis() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
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
        storage.firstInstallDate = System.currentTimeMillis()
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
        storage.firstInstallDate = System.currentTimeMillis()
        
        // When & Then
        assertTrue(storage.isTrialActive())
    }

    @Test
    fun `isTrialActive returns false for expired trial`() = runTest {
        // Given
        val storage = createTestStorage()
        val fortyDaysAgo = System.currentTimeMillis() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
        storage.firstInstallDate = fortyDaysAgo
        
        // When & Then
        assertFalse(storage.isTrialActive())
    }

    @Test
    fun `recordPurchase sets all purchase fields correctly`() = runTest {
        // Given
        val storage = createTestStorage()
        val productId = "test_product"
        val purchaseTime = System.currentTimeMillis()
        
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
        storage.firstInstallDate = System.currentTimeMillis()
        
        // When
        val state = storage.getEntitlementState()
        
        // Then
        assertEquals(EntitlementState.TRIAL, state)
    }

    @Test
    fun `getEntitlementState returns TRIAL_EXPIRED for expired trial`() = runTest {
        // Given
        val storage = createTestStorage()
        val fortyDaysAgo = System.currentTimeMillis() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
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
        val yesterday = System.currentTimeMillis() - SubscriptionStorage.MILLIS_PER_DAY
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
        storage.firstInstallDate = System.currentTimeMillis()
        
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
        storage.firstInstallDate = System.currentTimeMillis()
        
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
        val fortyDaysAgo = System.currentTimeMillis() - (40 * SubscriptionStorage.MILLIS_PER_DAY)
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
        storage.firstInstallDate = System.currentTimeMillis()
        storage.recordPurchase("test", PurchaseType.YEARLY_SUBSCRIPTION)
        
        // When
        storage.clearAll()
        
        // Then
        assertEquals(0L, storage.firstInstallDate)
        assertFalse(storage.isSubscribed)
        assertNull(storage.productId)
        assertEquals(PurchaseType.NONE, storage.purchaseType)
    }
}

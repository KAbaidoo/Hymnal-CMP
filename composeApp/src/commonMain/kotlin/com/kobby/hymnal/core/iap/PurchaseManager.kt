package com.kobby.hymnal.core.iap

import com.kobby.hymnal.presentation.screens.settings.PayPlan
import kotlinx.coroutines.flow.StateFlow

interface PurchaseManager {
    /**
     * Purchase a subscription plan.
     */
    fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit)
    
    /**
     * Check if the user has an active subscription.
     */
    fun isUserSubscribed(callback: (Boolean) -> Unit)
    
    /**
     * Navigate to platform-specific subscription management.
     */
    fun manageSubscription()
    
    /**
     * Restore previous purchases (important for reinstalls).
     */
    fun restorePurchases(callback: (Boolean) -> Unit)
    
    /**
     * Get current entitlement information including trial status.
     */
    fun getEntitlementInfo(): EntitlementInfo
    
    /**
     * Observable flow of entitlement state for reactive UI updates.
     */
    val entitlementState: StateFlow<EntitlementInfo>
    
    /**
     * Usage tracker for monitoring app usage and showing support prompts.
     */
    val usageTracker: UsageTrackingManager

    /**
     * Initialize the subscription manager (check trial, verify purchases).
     * Should be called on app startup.
     */
    fun initialize()
}

expect fun createSubscriptionManager(): PurchaseManager


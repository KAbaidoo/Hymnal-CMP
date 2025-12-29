package com.kobby.hymnal.core.iap

/**
 * Represents the current entitlement state of the user.
 */
enum class EntitlementState {
    /**
     * User is within the 30-day trial period and has not purchased.
     */
    TRIAL,
    
    /**
     * User has an active subscription or one-time purchase.
     * Note: One-time purchases (PurchaseType.ONE_TIME_PURCHASE) remain in this state forever.
     */
    SUBSCRIBED,
    
    /**
     * User's trial has expired and they have not purchased.
     */
    TRIAL_EXPIRED,
    
    /**
     * User's renewable subscription has expired.
     * Note: This only applies to renewable subscriptions (PurchaseType.YEARLY_SUBSCRIPTION),
     * not one-time purchases which never expire.
     */
    SUBSCRIPTION_EXPIRED,
    
    /**
     * No entitlement - fresh install or reinstall after trial expired.
     */
    NONE
}

/**
 * Represents the type of purchase the user has made.
 */
enum class PurchaseType {
    NONE,
    YEARLY_SUBSCRIPTION,
    ONE_TIME_PURCHASE
}

/**
 * Complete entitlement information for the user.
 */
data class EntitlementInfo(
    val state: EntitlementState,
    val purchaseType: PurchaseType,
    val trialDaysRemaining: Int?,
    val firstInstallDate: Long?,
    val purchaseDate: Long?,
    val expirationDate: Long?
) {
    val hasAccess: Boolean
        get() = state == EntitlementState.TRIAL || state == EntitlementState.SUBSCRIBED
    
    val isInTrial: Boolean
        get() = state == EntitlementState.TRIAL
    
    val needsPaywall: Boolean
        get() = state == EntitlementState.TRIAL_EXPIRED || 
                state == EntitlementState.SUBSCRIPTION_EXPIRED || 
                state == EntitlementState.NONE
}

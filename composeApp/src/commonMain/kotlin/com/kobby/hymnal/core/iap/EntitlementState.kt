package com.kobby.hymnal.core.iap

/**
 * Represents the current entitlement state of the user in the freemium model.
 */
enum class EntitlementState {
    /**
     * User has an active one-time purchase.
     * One-time purchases remain in this state forever (non-expiring).
     */
    SUBSCRIBED,
    
    /**
     * No entitlement - user has not supported the app.
     * In freemium model, users can still access core features.
     */
    NONE
}

/**
 * Premium features that require support contribution to access.
 * Core worship features (reading hymns, basic search) remain free.
 */
enum class PremiumFeature {
    /**
     * Ability to mark hymns as favorites
     */
    FAVORITES,

    /**
     * Ability to highlight text in hymns
     */
    HIGHLIGHTS,

    /**
     * Ability to customize font size and family
     */
    FONT_CUSTOMIZATION
}

/**
 * Represents the type of purchase the user has made.
 * Both support tiers are one-time purchases in the freemium model.
 */
enum class PurchaseType {
    NONE,
    ONE_TIME_PURCHASE
}

/**
 * Complete entitlement information for the user.
 */
data class EntitlementInfo(
    val state: EntitlementState,
    val purchaseType: PurchaseType,
    val trialDaysRemaining: Int?, // Deprecated - always null in freemium model
    val firstInstallDate: Long?, // Deprecated - always null in freemium model
    val purchaseDate: Long?,
    val expirationDate: Long? // Deprecated - always null for one-time purchases
) {
    /**
     * In freemium model, only users with one-time purchases have access to premium features.
     * Free users can access core features without any time limit.
     */
    val hasAccess: Boolean
        get() = state == EntitlementState.SUBSCRIBED

    /**
     * Deprecated - no trial period in freemium model.
     */
    @Deprecated("No trial in freemium model")
    val isInTrial: Boolean
        get() = false

    /**
     * Deprecated - no hard paywall in freemium model.
     * Support sheet is shown on feature access, not as a blocking wall.
     */
    @Deprecated("No hard paywall in freemium model")
    val needsPaywall: Boolean
        get() = false

    /**
     * Check if user can access a specific premium feature.
     * In the generous freemium model, users need to support to access premium features.
     */
    fun canAccessFeature(feature: PremiumFeature): Boolean {
        return state == EntitlementState.SUBSCRIBED
    }

    /**
     * Check if user has supported the app (made any purchase).
     */
    val hasSupported: Boolean
        get() = purchaseType != PurchaseType.NONE
}

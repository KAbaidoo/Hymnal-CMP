package com.kobby.hymnal.core.iap

/**
 * Represents the current entitlement state of the user.
 * In the new model, all features are free - this only tracks supporter status.
 */
enum class EntitlementState {
    /**
     * User has supported the app with a one-time donation.
     * Supporters receive relief from donation prompts for approximately one year.
     */
    SUPPORTED,

    /**
     * User has not yet supported the app.
     * All features remain accessible - support is optional.
     */
    NONE
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
 * In the new model, this only tracks supporter status for donation prompt management.
 */
data class EntitlementInfo(
    val state: EntitlementState,
    val purchaseType: PurchaseType,
    val purchaseDate: Long?,
    val expirationDate: Long? // Deprecated - always null for one-time donations
) {
    /**
     * Check if user has supported the app.
     * Supporters receive relief from donation prompts for approximately one year.
     * Note: All features are accessible regardless of support status.
     */
    val hasSupported: Boolean
        get() = state == EntitlementState.SUPPORTED

    /**
     * Legacy property for backwards compatibility.
     * In the new model, all users "have access" since everything is free.
     */
    @Deprecated("All features are now free. Use hasSupported to check supporter status.")
    val hasAccess: Boolean
        get() = true // Everyone has access to all features now
}

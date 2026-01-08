package com.kobby.hymnal.core.iap

import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock

/**
 * Manages persistent storage of subscription data.
 * Uses multiplatform-settings for cross-platform persistence.
 */
class PurchaseStorage(private val settings: Settings) {
    
    companion object Companion {
        // Keys for persistent storage
        private const val KEY_PURCHASE_DATE = "subscription_purchase_date"
        private const val KEY_PURCHASE_TYPE = "subscription_purchase_type"
        private const val KEY_PRODUCT_ID = "subscription_product_id"
        private const val KEY_EXPIRATION_DATE = "subscription_expiration_date"
        private const val KEY_IS_SUBSCRIBED = "subscription_is_subscribed"
        private const val KEY_LAST_VERIFICATION_TIME = "subscription_last_verification_time"
        
        // Usage tracking keys
        private const val KEY_HYMNS_READ_COUNT = "usage_hymns_read_count"
        private const val KEY_FEATURE_ACCESS_PREFIX = "usage_feature_access_"

        // Donation prompt tracking keys (new model)
        private const val KEY_DONATION_PROMPT_COUNT = "donation_prompt_count"
        private const val KEY_LAST_DONATION_PROMPT_TIMESTAMP = "last_donation_prompt_timestamp"
        private const val KEY_LAST_DONATION_DATE = "last_donation_date"
        private const val KEY_NEXT_PROMPT_THRESHOLD = "next_prompt_threshold"
        private const val KEY_HYMNS_SINCE_DONATION = "hymns_since_donation"
    }

    /**
     * Get or set the purchase date (timestamp in milliseconds).
     */
    var purchaseDate: Long?
        get() {
            val value = settings.getLong(KEY_PURCHASE_DATE, 0L)
            return if (value > 0) value else null
        }
        set(value) = settings.putLong(KEY_PURCHASE_DATE, value ?: 0L)
    
    /**
     * Get or set the purchase type.
     */
    var purchaseType: PurchaseType
        get() {
            val value = settings.getString(KEY_PURCHASE_TYPE, PurchaseType.NONE.name)
            return try {
                PurchaseType.valueOf(value)
            } catch (_: IllegalArgumentException) {
                PurchaseType.NONE
            }
        }
        set(value) = settings.putString(KEY_PURCHASE_TYPE, value.name)
    
    /**
     * Get or set the product ID of the active purchase.
     */
    var productId: String?
        get() = settings.getString(KEY_PRODUCT_ID, "")
        set(value) {
            if (value != null) {
                settings.putString(KEY_PRODUCT_ID, value)
            } else {
                settings.remove(KEY_PRODUCT_ID)
            }
        }
    
    /**
     * Get or set the expiration date for renewable subscriptions.
     */
    var expirationDate: Long?
        get() {
            val value = settings.getLong(KEY_EXPIRATION_DATE, 0L)
            return if (value > 0) value else null
        }
        set(value) = settings.putLong(KEY_EXPIRATION_DATE, value ?: 0L)
    
    /**
     * Get or set whether the user is currently subscribed.
     */
    var isSubscribed: Boolean
        get() = settings.getBoolean(KEY_IS_SUBSCRIBED, false)
        set(value) = settings.putBoolean(KEY_IS_SUBSCRIBED, value)
    
    /**
     * Get or set the last time we verified the subscription status with the platform.
     */
    var lastVerificationTime: Long
        get() = settings.getLong(KEY_LAST_VERIFICATION_TIME, 0L)
        set(value) = settings.putLong(KEY_LAST_VERIFICATION_TIME, value)
    
    /**
     * Record a successful purchase.
     */
    fun recordPurchase(
        productId: String,
        purchaseType: PurchaseType,
        purchaseTimestamp: Long = Clock.System.now().toEpochMilliseconds(),
        expirationTimestamp: Long? = null
    ) {
        this.productId = productId
        this.purchaseType = purchaseType
        this.purchaseDate = purchaseTimestamp
        this.expirationDate = expirationTimestamp
        this.isSubscribed = true
        this.lastVerificationTime = Clock.System.now().toEpochMilliseconds()
    }
    
    /**
     * Clear subscription data (for testing or if subscription is no longer valid).
     */
    fun clearSubscription() {
        isSubscribed = false
        purchaseDate = null
        purchaseType = PurchaseType.NONE
        productId = null
        expirationDate = null
    }
    
    /**
     * Clear all subscription data (for testing purposes).
     */
    fun clearAll() {
        settings.remove(KEY_PURCHASE_DATE)
        settings.remove(KEY_PURCHASE_TYPE)
        settings.remove(KEY_PRODUCT_ID)
        settings.remove(KEY_EXPIRATION_DATE)
        settings.remove(KEY_IS_SUBSCRIBED)
        settings.remove(KEY_LAST_VERIFICATION_TIME)
    }
    
    /**
     * Get current entitlement state based on stored data and current time.
     */
    fun getEntitlementState(): EntitlementState {
        // Check if user has active one-time purchase (donation)
        if (isSubscribed && purchaseType == PurchaseType.ONE_TIME_PURCHASE) {
            return EntitlementState.SUPPORTED
        }
        
        // No purchase = no support (but all features still accessible)
        return EntitlementState.NONE
    }
    
    /**
     * Get complete entitlement information.
     */
    fun getEntitlementInfo(): EntitlementInfo {
        return EntitlementInfo(
            state = getEntitlementState(),
            purchaseType = purchaseType,
            purchaseDate = purchaseDate,
            expirationDate = expirationDate
        )
    }

    // Donation prompt tracking methods

    /**
     * Get or set the number of times donation prompt has been shown.
     */
    var donationPromptCount: Int
        get() = settings.getInt(KEY_DONATION_PROMPT_COUNT, 0)
        set(value) = settings.putInt(KEY_DONATION_PROMPT_COUNT, value)

    /**
     * Get or set the timestamp of the last donation prompt shown.
     */
    var lastDonationPromptTimestamp: Long?
        get() {
            val value = settings.getLong(KEY_LAST_DONATION_PROMPT_TIMESTAMP, 0L)
            return if (value > 0) value else null
        }
        set(value) = settings.putLong(KEY_LAST_DONATION_PROMPT_TIMESTAMP, value ?: 0L)

    /**
     * Get or set the timestamp of the last donation made.
     */
    var lastDonationDate: Long?
        get() {
            val value = settings.getLong(KEY_LAST_DONATION_DATE, 0L)
            return if (value > 0) value else null
        }
        set(value) = settings.putLong(KEY_LAST_DONATION_DATE, value ?: 0L)

    /**
     * Get or set the next hymn count threshold for showing donation prompt.
     */
    var nextPromptThreshold: Int
        get() = settings.getInt(KEY_NEXT_PROMPT_THRESHOLD, 10) // Default to 10
        set(value) = settings.putInt(KEY_NEXT_PROMPT_THRESHOLD, value)

    /**
     * Get or set hymns read since last donation (for supporters).
     */
    var hymnsSinceDonation: Int
        get() = settings.getInt(KEY_HYMNS_SINCE_DONATION, 0)
        set(value) = settings.putInt(KEY_HYMNS_SINCE_DONATION, value)

    /**
     * Check if yearly reminder should be shown to supporters.
     * Returns true if 365 days have passed since last donation.
     */
    fun shouldShowYearlyReminder(): Boolean {
        val lastDonation = lastDonationDate ?: return false
        val oneYearInMillis = 365L * 24 * 60 * 60 * 1000
        val currentTime = Clock.System.now().toEpochMilliseconds()
        return (currentTime - lastDonation) >= oneYearInMillis
    }

    /**
     * Record that a donation was made.
     */
    fun recordDonation() {
        lastDonationDate = Clock.System.now().toEpochMilliseconds()
        donationPromptCount = 0
        hymnsSinceDonation = 0
        nextPromptThreshold = 50 // First reminder after 365 days starts at 50 hymns
    }

    /**
     * Calculate the next prompt threshold based on prompt count and supporter status.
     */
    fun calculateNextThreshold(isSupporter: Boolean): Int {
        return if (isSupporter) {
            // Supporters (yearly reminders): 50, 100, 200 (less aggressive)
            when (donationPromptCount) {
                0 -> 50
                1 -> 100
                else -> 200
            }
        } else {
            // Non-supporters: 10, 25, 50, 100, 200, 400 (capped)
            when (donationPromptCount) {
                0 -> 10
                1 -> 25
                2 -> 50
                3 -> 100
                4 -> 200
                else -> 400 // Cap at 400
            }
        }
    }

    // Usage tracking methods

    /**
     * Get or set the number of hymns read by the user.
     */
    var hymnsReadCount: Int
        get() = settings.getInt(KEY_HYMNS_READ_COUNT, 0)
        set(value) = settings.putInt(KEY_HYMNS_READ_COUNT, value)
}

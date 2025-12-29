package com.kobby.hymnal.core.iap

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

/**
 * Manages persistent storage of subscription and trial data.
 * Uses multiplatform-settings for cross-platform persistence.
 */
class SubscriptionStorage(private val settings: Settings) {
    
    companion object {
        // Keys for persistent storage
        private const val KEY_FIRST_INSTALL_DATE = "subscription_first_install_date"
        private const val KEY_PURCHASE_DATE = "subscription_purchase_date"
        private const val KEY_PURCHASE_TYPE = "subscription_purchase_type"
        private const val KEY_PRODUCT_ID = "subscription_product_id"
        private const val KEY_EXPIRATION_DATE = "subscription_expiration_date"
        private const val KEY_IS_SUBSCRIBED = "subscription_is_subscribed"
        private const val KEY_LAST_VERIFICATION_TIME = "subscription_last_verification_time"
        
        // Trial period constants
        const val TRIAL_DURATION_DAYS = 30
        const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    }
    
    /**
     * Get or set the first install date (timestamp in milliseconds).
     * This is used to track the start of the trial period.
     */
    var firstInstallDate: Long
        get() = settings.getLong(KEY_FIRST_INSTALL_DATE, 0L)
        set(value) = settings.putLong(KEY_FIRST_INSTALL_DATE, value)
    
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
            } catch (e: IllegalArgumentException) {
                PurchaseType.NONE
            }
        }
        set(value) = settings.putString(KEY_PURCHASE_TYPE, value.name)
    
    /**
     * Get or set the product ID of the active purchase.
     */
    var productId: String?
        get() = settings.getString(KEY_PRODUCT_ID, null)
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
     * Initialize first install date if not already set.
     * Should be called on app startup.
     */
    fun initializeFirstInstallIfNeeded() {
        if (firstInstallDate == 0L) {
            firstInstallDate = System.currentTimeMillis()
        }
    }
    
    /**
     * Calculate days remaining in trial period.
     * Returns null if trial has expired or user has purchased.
     */
    fun getTrialDaysRemaining(): Int? {
        if (isSubscribed) return null
        if (firstInstallDate == 0L) return null
        
        val currentTime = System.currentTimeMillis()
        val daysSinceInstall = (currentTime - firstInstallDate) / MILLIS_PER_DAY
        val daysRemaining = TRIAL_DURATION_DAYS - daysSinceInstall.toInt()
        
        return if (daysRemaining > 0) daysRemaining else 0
    }
    
    /**
     * Check if the trial period is active.
     */
    fun isTrialActive(): Boolean {
        val daysRemaining = getTrialDaysRemaining()
        return daysRemaining != null && daysRemaining > 0
    }
    
    /**
     * Record a successful purchase.
     */
    fun recordPurchase(
        productId: String,
        purchaseType: PurchaseType,
        purchaseTimestamp: Long = System.currentTimeMillis(),
        expirationTimestamp: Long? = null
    ) {
        this.productId = productId
        this.purchaseType = purchaseType
        this.purchaseDate = purchaseTimestamp
        this.expirationDate = expirationTimestamp
        this.isSubscribed = true
        this.lastVerificationTime = System.currentTimeMillis()
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
     * Clear all subscription and trial data (for testing purposes).
     */
    fun clearAll() {
        settings.remove(KEY_FIRST_INSTALL_DATE)
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
        val currentTime = System.currentTimeMillis()
        
        // Check if user has active subscription or one-time purchase
        if (isSubscribed) {
            // One-time purchases never expire
            if (purchaseType == PurchaseType.ONE_TIME_PURCHASE) {
                return EntitlementState.SUBSCRIBED
            }
            
            // For renewable subscriptions, check if expired
            expirationDate?.let { expiration ->
                if (currentTime > expiration) {
                    return EntitlementState.SUBSCRIPTION_EXPIRED
                }
            }
            return EntitlementState.SUBSCRIBED
        }
        
        // Check trial status
        if (firstInstallDate > 0) {
            val daysSinceInstall = (currentTime - firstInstallDate) / MILLIS_PER_DAY
            if (daysSinceInstall < TRIAL_DURATION_DAYS) {
                return EntitlementState.TRIAL
            } else {
                return EntitlementState.TRIAL_EXPIRED
            }
        }
        
        return EntitlementState.NONE
    }
    
    /**
     * Get complete entitlement information.
     */
    fun getEntitlementInfo(): EntitlementInfo {
        return EntitlementInfo(
            state = getEntitlementState(),
            purchaseType = purchaseType,
            trialDaysRemaining = getTrialDaysRemaining(),
            firstInstallDate = if (firstInstallDate > 0) firstInstallDate else null,
            purchaseDate = purchaseDate,
            expirationDate = expirationDate
        )
    }
}

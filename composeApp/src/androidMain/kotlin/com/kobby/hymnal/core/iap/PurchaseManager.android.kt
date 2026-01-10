package com.kobby.hymnal.core.iap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.android.billingclient.api.BillingClient
import com.kobby.hymnal.presentation.screens.settings.PayPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class AndroidPurchaseManager(
    private val context: Context,
    private val billingHelper: BillingHelper,
    private val storage: PurchaseStorage
) : PurchaseManager {
    
    private val _entitlementState = MutableStateFlow(storage.getEntitlementInfo())
    override val entitlementState: StateFlow<EntitlementInfo> = _entitlementState.asStateFlow()

    override val usageTracker: UsageTrackingManager = UsageTrackingManager(storage)

    override fun initialize() {
        // Initialize usage tracker
        usageTracker.initialize()

        // Check current subscription status from platform
        refreshEntitlementState()
    }

    override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
        (context as? Activity)?.let { activity ->
            // Both tiers are one-time purchases (non-consumable in-app products)
            val productId = when (plan) {
                PayPlan.SupportBasic -> billingHelper.SUPPORT_BASIC
                PayPlan.SupportGenerous -> billingHelper.SUPPORT_GENEROUS
            }
            billingHelper.purchaseProduct(productId, BillingClient.ProductType.INAPP, activity) { success ->
                if (success) {
                    // Record purchase in storage - both are one-time purchases
                    storage.recordPurchase(
                        productId = productId,
                        purchaseType = PurchaseType.ONE_TIME_PURCHASE
                    )
                    // Record donation made to reset prompt counters and start grace period
                    usageTracker.recordDonationMade()
                    refreshEntitlementState()
                }
                callback(success)
            }
        } ?: callback(false)
    }

    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
        billingHelper.checkSubscriptionStatus { hasPurchase, productId, purchaseTime ->
            // Update storage with current state
            storage.isSubscribed = hasPurchase
            storage.lastVerificationTime = Clock.System.now().toEpochMilliseconds()

            // If we have a purchaseTime and no local purchaseDate, record it (do not override existing)
            if (hasPurchase && purchaseTime != null && storage.purchaseDate == null) {
                val pid = productId ?: billingHelper.SUPPORT_BASIC
                storage.recordPurchase(productId = pid, purchaseType = PurchaseType.ONE_TIME_PURCHASE, purchaseTimestamp = purchaseTime)
            }

            // In freemium model, only actual subscribers have access
            refreshEntitlementState()
            callback(hasPurchase)
        }
    }

    override fun manageSubscription() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    
    override fun restorePurchases(callback: (Boolean) -> Unit) {
        // On Android, restoration is automatic via queryPurchasesAsync
        billingHelper.checkSubscriptionStatus { hasPurchase, productId, purchaseTime ->
            if (hasPurchase) {
                // If not already recorded locally, record the purchase using the found product id and timestamp if available
                if (!storage.isSubscribed) {
                    val pid = productId ?: billingHelper.SUPPORT_BASIC
                    val ts = purchaseTime ?: Clock.System.now().toEpochMilliseconds()
                    storage.recordPurchase(
                        productId = pid,
                        purchaseType = PurchaseType.ONE_TIME_PURCHASE,
                        purchaseTimestamp = ts
                    )
                } else {
                    // If subscribed but no purchaseDate recorded, set from purchaseTime if available, else fallback
                    if (storage.purchaseDate == null) {
                        storage.purchaseDate = purchaseTime ?: Clock.System.now().toEpochMilliseconds()
                    }
                }

                // Treat restoration as a donation recovery: start grace period and reset counters
                usageTracker.recordDonationMade()

                // Update entitlement state and return success
                refreshEntitlementState()
                callback(true)
            } else {
                callback(false)
            }
        }
    }
    
    override fun getEntitlementInfo(): EntitlementInfo {
        return storage.getEntitlementInfo()
    }
    
    private fun refreshEntitlementState() {
        _entitlementState.value = storage.getEntitlementInfo()
    }
}

actual fun createSubscriptionManager(): PurchaseManager {
    throw IllegalStateException("Use Koin for dependency injection on Android")
}

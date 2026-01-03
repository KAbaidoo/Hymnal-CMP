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

class AndroidSubscriptionManager(
    private val context: Context,
    private val billingHelper: BillingHelper,
    private val storage: SubscriptionStorage
) : SubscriptionManager {
    
    private val _entitlementState = MutableStateFlow(storage.getEntitlementInfo())
    override val entitlementState: StateFlow<EntitlementInfo> = _entitlementState.asStateFlow()

    override fun initialize() {
        // Initialize first install date if needed
        storage.initializeFirstInstallIfNeeded()
        
        // Check current subscription status from platform
        refreshEntitlementState()
    }

    override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
        (context as? Activity)?.let { activity ->
            val (productId, productType) = when (plan) {
                PayPlan.Yearly -> billingHelper.YEARLY_SUBSCRIPTION to BillingClient.ProductType.SUBS
                PayPlan.OneTime -> billingHelper.ONETIME_PURCHASE to BillingClient.ProductType.INAPP
            }
            billingHelper.purchaseProduct(productId, productType, activity) { success ->
                if (success) {
                    // Record purchase in storage
                    val purchaseType = when (plan) {
                        PayPlan.Yearly -> PurchaseType.YEARLY_SUBSCRIPTION
                        PayPlan.OneTime -> PurchaseType.ONE_TIME_PURCHASE
                    }
                    storage.recordPurchase(
                        productId = productId,
                        purchaseType = purchaseType
                    )
                    refreshEntitlementState()
                }
                callback(success)
            }
        } ?: callback(false)
    }

    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
        billingHelper.checkSubscriptionStatus { isSubscribed ->
            // Update storage with current state
            storage.isSubscribed = isSubscribed
            storage.lastVerificationTime = System.currentTimeMillis()
            
            // Check if user has access (either subscribed or in trial)
            val hasAccess = isSubscribed || storage.isTrialActive()
            refreshEntitlementState()
            callback(hasAccess)
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
        billingHelper.checkSubscriptionStatus { isSubscribed ->
            if (isSubscribed) {
                // User has active subscription, update storage
                storage.isSubscribed = true
                storage.lastVerificationTime = System.currentTimeMillis()
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

actual fun createSubscriptionManager(): SubscriptionManager {
    throw IllegalStateException("Use Koin for dependency injection on Android")
}

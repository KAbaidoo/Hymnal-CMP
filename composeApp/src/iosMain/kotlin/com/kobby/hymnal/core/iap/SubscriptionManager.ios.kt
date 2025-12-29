
package com.kobby.hymnal.core.iap

import com.kobby.hymnal.presentation.screens.settings.PayPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IosSubscriptionManager(
    private val storage: SubscriptionStorage
) : SubscriptionManager {

    companion object {
        const val YEARLY_SUBSCRIPTION_ID = "ios_yearly_subscription"
        const val ONETIME_PURCHASE_ID = "ios_onetime_purchase"
    }
    
    private val _entitlementState = MutableStateFlow(storage.getEntitlementInfo())
    override val entitlementState: StateFlow<EntitlementInfo> = _entitlementState.asStateFlow()

    init {
        nativeSubscriptionProvider?.fetchSubscriptions()
    }
    
    override fun initialize() {
        // Initialize first install date if needed
        storage.initializeFirstInstallIfNeeded()
        
        // Check current subscription status from platform
        refreshEntitlementState()
    }

    override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
        val productId = when (plan) {
            PayPlan.Yearly -> YEARLY_SUBSCRIPTION_ID
            PayPlan.OneTime -> ONETIME_PURCHASE_ID
        }

        nativeSubscriptionProvider?.purchaseSubscription(
            productId = productId,
            callback = { success ->
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
        )
    }

    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
        nativeSubscriptionProvider?.isUserSubscribed { isSubscribed ->
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
        nativeSubscriptionProvider?.manageSubscription()
    }
    
    override fun restorePurchases(callback: (Boolean) -> Unit) {
        nativeSubscriptionProvider?.restorePurchases { success ->
            if (success) {
                // Refresh subscription status after restoration
                isUserSubscribed { _ ->
                    callback(true)
                }
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
    throw IllegalStateException("Use Koin for dependency injection on iOS")
}

interface NativeSubscriptionProvider {
    fun isUserSubscribed(callback: (Boolean) -> Unit)
    fun fetchSubscriptions()
    fun manageSubscription()
    fun purchaseSubscription(productId: String, callback: (Boolean) -> Unit): Boolean
    fun restorePurchases(callback: (Boolean) -> Unit)
}

private var nativeSubscriptionProvider: NativeSubscriptionProvider? = null

fun initializeNativeSubscriptionProvider(provider: NativeSubscriptionProvider) {
    nativeSubscriptionProvider = provider
}
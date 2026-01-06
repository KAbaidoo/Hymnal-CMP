package com.kobby.hymnal.core.iap

import com.kobby.hymnal.presentation.screens.settings.PayPlan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class IosPurchaseManager(
    private val storage: PurchaseStorage
) : PurchaseManager {

    companion object {
        const val SUPPORT_BASIC_ID = "support_basic"
        const val SUPPORT_GENEROUS_ID = "support_generous"
    }
    
    private val _entitlementState = MutableStateFlow(storage.getEntitlementInfo())
    override val entitlementState: StateFlow<EntitlementInfo> = _entitlementState.asStateFlow()

    override val usageTracker: UsageTrackingManager = UsageTrackingManager(storage)

    init {
        nativePurchaseProvider?.fetchPurchases()
    }
    
    override fun initialize() {
        // Initialize usage tracker
        usageTracker.initialize()

        // Check current purchase status from platform
        refreshEntitlementState()
    }

    override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
        val productId = when (plan) {
            PayPlan.SupportBasic -> SUPPORT_BASIC_ID
            PayPlan.SupportGenerous -> SUPPORT_GENEROUS_ID
        }

        nativePurchaseProvider?.purchasePurchase(
            productId = productId,
            callback = { success ->
                if (success) {
                    // Record purchase in storage - both are one-time purchases
                    storage.recordPurchase(
                        productId = productId,
                        purchaseType = PurchaseType.ONE_TIME_PURCHASE
                    )
                    // Reset hymn read count after support
                    usageTracker.resetHymnReadCount()
                    refreshEntitlementState()
                }
                callback(success)
            }
        )
    }

    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
        nativePurchaseProvider?.isUserPurchased { isPurchased ->
            // Update storage with current state
            storage.isSubscribed = isPurchased
            storage.lastVerificationTime = Clock.System.now().toEpochMilliseconds()

            // In freemium model, only users with purchases have access
            refreshEntitlementState()
            callback(isPurchased)
        }
    }

    override fun manageSubscription() {
        nativePurchaseProvider?.managePurchase()
    }
    
    override fun restorePurchases(callback: (Boolean) -> Unit) {
        nativePurchaseProvider?.restorePurchases { success ->
            if (success) {
                // Refresh purchase status after restoration
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

actual fun createSubscriptionManager(): PurchaseManager {
    throw IllegalStateException("Use Koin for dependency injection on iOS")
}

interface NativePurchaseProvider {
    fun isUserPurchased(callback: (Boolean) -> Unit)
    fun fetchPurchases()
    fun managePurchase()
    fun purchasePurchase(productId: String, callback: (Boolean) -> Unit): Boolean
    fun restorePurchases(callback: (Boolean) -> Unit)
}

private var nativePurchaseProvider: NativePurchaseProvider? = null

fun initializeNativePurchaseProvider(provider: NativePurchaseProvider) {
    nativePurchaseProvider = provider
}
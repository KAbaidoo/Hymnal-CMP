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

    override fun makePurchase(plan: PayPlan, callback: (Boolean) -> Unit) {
        val productId = when (plan) {
            PayPlan.SupportBasic -> SUPPORT_BASIC_ID
            PayPlan.SupportGenerous -> SUPPORT_GENEROUS_ID
        }

        nativePurchaseProvider?.makePurchase(
            productId = productId,
            callback = { success ->
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
                // Try to get detailed restored purchase info (productId,timestamp;...)
                nativePurchaseProvider?.getRestoredPurchasesInfo { info ->
                    if (info != null) {
                        // Parse the string
                        val raw = info
                        val entries = raw.split(';').mapNotNull { entry ->
                            val parts = entry.split(',')
                            if (parts.size >= 2) {
                                val pid = parts[0]
                                val ts = parts[1].toLongOrNull()
                                if (ts != null) Pair(pid, ts) else null
                            } else null
                        }

                        if (entries.isNotEmpty()) {
                            // Choose the most recent purchase timestamp entry
                            val latest = entries.maxByOrNull { it.second }!!
                            val pid = latest.first
                            val ts = latest.second

                            // Record exact product and timestamp if not already recorded
                            if (!storage.isSubscribed) {
                                storage.recordPurchase(
                                    productId = pid,
                                    purchaseType = PurchaseType.ONE_TIME_PURCHASE,
                                    purchaseTimestamp = ts
                                )
                            } else {
                                if (storage.purchaseDate == null) {
                                    storage.purchaseDate = ts
                                }
                            }

                            usageTracker.recordDonationMade()
                            refreshEntitlementState()
                            callback(true)
                            return@getRestoredPurchasesInfo
                        }
                    }

                    // Fallback: use previous flow (check boolean existence)
                    isUserSubscribed { isPurchased ->
                        if (isPurchased && !storage.isSubscribed) {
                            storage.recordPurchase(
                                productId = SUPPORT_BASIC_ID,
                                purchaseType = PurchaseType.ONE_TIME_PURCHASE,
                                purchaseTimestamp = Clock.System.now().toEpochMilliseconds()
                            )
                        } else {
                            if (storage.isSubscribed && storage.purchaseDate == null) {
                                storage.purchaseDate = Clock.System.now().toEpochMilliseconds()
                            }
                        }
                        usageTracker.recordDonationMade()
                        callback(true)
                    }
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

actual fun createPurchaseManager(): PurchaseManager {
    throw IllegalStateException("Use Koin for dependency injection on iOS")
}

interface NativePurchaseProvider {
    fun isUserPurchased(callback: (Boolean) -> Unit)
    fun fetchPurchases()
    fun managePurchase()
    fun makePurchase(productId: String, callback: (Boolean) -> Unit): Boolean
    fun restorePurchases(callback: (Boolean) -> Unit)
    // New: return a semicolon-separated list of restored purchases in the format "productId,timestampMillis;productId,timestampMillis"
    fun getRestoredPurchasesInfo(callback: (String?) -> Unit)
}

private var nativePurchaseProvider: NativePurchaseProvider? = null

fun initializeNativePurchaseProvider(provider: NativePurchaseProvider) {
    nativePurchaseProvider = provider
}
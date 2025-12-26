
package com.kobby.hymnal.core.iap

import com.kobby.hymnal.presentation.screens.settings.PayPlan

class IosSubscriptionManager : SubscriptionManager {

    companion object {
        const val YEARLY_SUBSCRIPTION_ID = "ios_yearly_subscription"
        const val ONETIME_SUBSCRIPTION_ID = "ios_onetime_subscription"
    }

    init {
        nativeSubscriptionProvider?.fetchSubscriptions()
    }

     override fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit) {
         val productId = when (plan) {
             PayPlan.Yearly -> YEARLY_SUBSCRIPTION_ID
             PayPlan.OneTime -> ONETIME_SUBSCRIPTION_ID
         }

         nativeSubscriptionProvider?.purchaseSubscription(
             productId = productId,
             callback = callback
         )
    }

    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
     nativeSubscriptionProvider?.isUserSubscribed(callback = callback)

    }

     override fun manageSubscription() {
         nativeSubscriptionProvider?.manageSubscription()
    }

}

actual fun createSubscriptionManager(): SubscriptionManager {
    throw IllegalStateException("Use Koin for dependency injection on iOS")
}

interface NativeSubscriptionProvider {
    fun isUserSubscribed(callback: (Boolean) -> Unit )
    fun fetchSubscriptions()
    fun manageSubscription()
    fun purchaseSubscription(productId: String, callback: (Boolean) -> Unit ): Boolean

}

private var nativeSubscriptionProvider: NativeSubscriptionProvider? = null

fun initializeNativeSubscriptionProvider(provider: NativeSubscriptionProvider) {
    nativeSubscriptionProvider = provider
}
package com.kobby.hymnal.core.iap

class IosSubscriptionManager : SubscriptionManager {

    init {
        nativeSubscriptionProvider?.fetchSubscriptions()
    }

     override fun purchaseSubscription(callback: (Boolean) -> Unit) {
         nativeSubscriptionProvider?.purchaseSubscription( callback = callback)
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
    fun purchaseSubscription(callback: (Boolean) -> Unit ): Boolean

}

private var nativeSubscriptionProvider: NativeSubscriptionProvider? = null

fun initializeNativeSubscriptionProvider(provider: NativeSubscriptionProvider) {
    nativeSubscriptionProvider = provider
}
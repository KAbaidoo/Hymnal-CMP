package com.kobby.hymnal.core.iap

interface SubscriptionManager {
    fun purchaseSubscription(callback: (Boolean) -> Unit)
    fun isUserSubscribed(callback: (Boolean) -> Unit)
    fun manageSubscription()
}

expect fun createSubscriptionManager(): SubscriptionManager
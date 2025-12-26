package com.kobby.hymnal.core.iap

import com.kobby.hymnal.presentation.screens.settings.PayPlan

interface SubscriptionManager {
    fun purchaseSubscription(plan: PayPlan, callback: (Boolean) -> Unit)
    fun isUserSubscribed(callback: (Boolean) -> Unit)
    fun manageSubscription()
}

expect fun createSubscriptionManager(): SubscriptionManager


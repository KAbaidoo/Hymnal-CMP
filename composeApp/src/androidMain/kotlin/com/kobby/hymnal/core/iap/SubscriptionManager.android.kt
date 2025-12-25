package com.kobby.hymnal.core.iap

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri

class AndroidSubscriptionManager(
    private val context: Context,
    private val billingHelper: BillingHelper
) : SubscriptionManager {

    override fun purchaseSubscription(callback: (Boolean) -> Unit) {
        (context as? Activity)?.let {
            billingHelper.purchaseSubscription(it, callback)
        } ?: callback(false)
    }

    override fun isUserSubscribed(callback: (Boolean) -> Unit) {
        billingHelper.checkSubscriptionStatus(callback)
    }

    override fun manageSubscription() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/account/subscriptions")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}

actual fun createSubscriptionManager(): SubscriptionManager {
    throw IllegalStateException("Use Koin for dependency injection on Android")
}

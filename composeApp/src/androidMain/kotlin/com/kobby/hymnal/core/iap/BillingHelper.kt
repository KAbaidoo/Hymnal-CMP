package com.kobby.hymnal.core.iap

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams

class BillingHelper(private val context: Context) {

    val PREMIUM ="premium_subscription"
    val TAG = BillingHelper::class.simpleName
    var purchaseCallback:((isSuccess:Boolean)->Unit)? = null


    var params: PendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enableOneTimeProducts()
        .enablePrepaidPlans()
        .build()

    private var billingClient: BillingClient

    init {
        billingClient = BillingClient.newBuilder(context)
            .enablePendingPurchases(params)
            .setListener { billingResult, purchases ->
                Log.d("enablePendingPurchases", "BillingClient creation result : ${billingResult.responseCode}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    handlePurchase(purchases)
                }
            }.build()
    }

    private fun connectPlayStore(callback: (isConnected:Boolean) -> Unit) {
        if (billingClient.isReady) {
            callback.invoke(true)
        } else {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.d(TAG, "startConnection onBillingSetupFinished")
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // BillingClient is ready
                        Log.d(TAG, "BillingClient is ready")
                        callback.invoke(true)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(TAG, "startConnection onBillingServiceDisconnected")
                }
            })
        }
    }

    fun checkSubscriptionStatus(callback: (Boolean) -> Unit) {
        Log.d(TAG, "checkSubscriptionStatus")
        connectPlayStore {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
                Log.d(TAG, "queryPurchasesAsync callback: ${billingResult.responseCode}, ${purchases.size}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val isSubscribed = purchases.any { it.products.contains(PREMIUM) }
                    callback(isSubscribed)
                } else {
                    callback(false)
                }
            }
        }

    }


    fun purchaseSubscription(activity: Activity, callback: (Boolean) -> Unit) {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PREMIUM)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList.first()
                val offerToken = productDetails.subscriptionOfferDetails?.first()?.offerToken
                    ?: return@queryProductDetailsAsync

                val billingParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(
                        listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .setOfferToken(offerToken)
                                .build()
                        )
                    )
                    .build()

                purchaseCallback = callback
                billingClient.launchBillingFlow(activity, billingParams)
            }
        }
    }

    private fun handlePurchase(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                // Grant subscription benefits
                Log.d("BillingManager", "Subscription is active: ${purchase.products}")
                acknowledgePurchase(purchase)
                purchaseCallback?.invoke(true)
                purchaseCallback = null
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingManager", "Purchase acknowledged")
                }
            }
        }
    }

}
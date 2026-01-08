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

    // Freemium model product IDs - Both are one-time purchases
    val SUPPORT_BASIC = "support_basic"           // GH₵ 10 / $0.99 - One-time purchase
    val SUPPORT_GENEROUS = "support_generous"     // GH₵ 20 / $1.99 - One-time purchase
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
                Log.d(TAG, "Purchase listener triggered: ${billingResult.responseCode}")

                when (billingResult.responseCode) {
                    BillingClient.BillingResponseCode.OK -> {
                        if (purchases != null) {
                            handlePurchase(purchases)
                        }
                    }
                    BillingClient.BillingResponseCode.USER_CANCELED -> {
                        Log.d(TAG, "User canceled the purchase")
                        purchaseCallback?.invoke(false)
                        purchaseCallback = null
                    }
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                        Log.d(TAG, "Item already owned")
                        purchaseCallback?.invoke(true)
                        purchaseCallback = null
                    }
                    else -> {
                        Log.e(TAG, "Purchase failed: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                        purchaseCallback?.invoke(false)
                        purchaseCallback = null
                    }
                }
            }
            .enableAutoServiceReconnection()
            .build()
    }

    private fun connectPlayStore(callback: (isConnected:Boolean) -> Unit) {
        if (billingClient.isReady) {
            callback.invoke(true)
        } else {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    Log.d(TAG, "startConnection onBillingSetupFinished: ${billingResult.responseCode}")
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        // BillingClient is ready
                        Log.d(TAG, "BillingClient is ready")
                        callback.invoke(true)
                    } else {
                        // Connection failed
                        Log.e(TAG, "BillingClient setup failed: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                        callback.invoke(false)
                    }
                }

                override fun onBillingServiceDisconnected() {
                    Log.d(TAG, "startConnection onBillingServiceDisconnected")
                    // Note: Will auto-reconnect due to enableAutoServiceReconnection()
                }
            })
        }
    }

    // Modified: returns (hasPurchase, productIdFound?)
    fun checkSubscriptionStatus(callback: (Boolean, String?) -> Unit) {
        Log.d(TAG, "checkPurchaseStatus - checking one-time purchases")
        connectPlayStore { isConnected ->
            if (!isConnected) {
                Log.e(TAG, "Failed to connect to Play Store for purchase check")
                callback(false, null)
                return@connectPlayStore
            }

            // Check one-time purchases - both tiers are INAPP products
            val inappParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            billingClient.queryPurchasesAsync(inappParams) { billingResult, purchases ->
                Log.d(TAG, "queryPurchasesAsync INAPP callback: ${billingResult.responseCode}, ${purchases.size}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Determine which product was purchased if any
                    var foundProduct: String? = null
                    val hasPurchase = purchases.any {
                        when {
                            it.products.contains(SUPPORT_BASIC) -> {
                                foundProduct = SUPPORT_BASIC
                                true
                            }
                            it.products.contains(SUPPORT_GENEROUS) -> {
                                foundProduct = SUPPORT_GENEROUS
                                true
                            }
                            else -> false
                        }
                    }
                    callback(hasPurchase, foundProduct)
                } else {
                    Log.e(TAG, "Failed to query inapp purchases: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    callback(false, null)
                }
            }
        }
    }


    fun purchaseProduct(productId: String, productType: String, activity: Activity, callback: (Boolean) -> Unit) {
        // First, ensure we're connected to the Play Store
        connectPlayStore { isConnected ->
            if (!isConnected) {
                Log.e(TAG, "Failed to connect to Play Store")
                callback(false)
                return@connectPlayStore
            }

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(productId)
                            .setProductType(productType)
                            .build()
                    )
                )
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && queryProductDetailsResult.productDetailsList.isNotEmpty()) {
                    val productDetails = queryProductDetailsResult.productDetailsList.first()

                    val billingParamsBuilder = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                            listOf(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(productDetails)
                                    .apply {
                                        if (productType == BillingClient.ProductType.SUBS) {
                                            val offerToken = productDetails.subscriptionOfferDetails?.first()?.offerToken
                                            if (offerToken == null) {
                                                Log.e(TAG, "No offer token found for subscription product")
                                                callback(false)
                                                return@queryProductDetailsAsync
                                            }
                                            setOfferToken(offerToken)
                                        }
                                    }
                                    .build()
                            )
                        )
                        .build()

                    purchaseCallback = callback
                    val launchResult = billingClient.launchBillingFlow(activity, billingParamsBuilder)

                    if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                        Log.e(TAG, "Failed to launch billing flow: ${launchResult.responseCode} - ${launchResult.debugMessage}")
                        purchaseCallback = null
                        callback(false)
                    }
                } else {
                    Log.e(TAG, "Failed to query product details: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    callback(false)
                }
            }
        }
    }

    private fun handlePurchase(purchases: List<Purchase>) {
        for (purchase in purchases) {
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    // Grant support benefits for one-time purchases
                    Log.d(TAG, "Purchase is active: ${purchase.products}")
                    // One-time purchases don't need acknowledgment
                    purchaseCallback?.invoke(true)
                    purchaseCallback = null
                }
                Purchase.PurchaseState.PENDING -> {
                    Log.d(TAG, "Purchase is pending: ${purchase.products}")
                    // Optionally notify user that purchase is pending
                    // Don't invoke callback yet - wait for final state
                }
                Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                    Log.w(TAG, "Purchase state is unspecified: ${purchase.products}")
                    purchaseCallback?.invoke(false)
                    purchaseCallback = null
                }
            }
        }
    }

    /**
     * Clean up resources and end the billing client connection.
     * Should be called when the BillingHelper is no longer needed.
     */
    fun endConnection() {
        Log.d(TAG, "Ending billing client connection")
        purchaseCallback = null
        billingClient.endConnection()
    }

}
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
    val SUPPORT_BASIC = "support_basic"
    val SUPPORT_GENEROUS = "support_generous"
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

    // Modified: returns (hasPurchase, productIdFound?, purchaseTimestampMillis?)
    fun checkSubscriptionStatus(callback: (Boolean, String?, Long?) -> Unit) {
        Log.d(TAG, "checkPurchaseStatus - checking one-time purchases")
        connectPlayStore { isConnected ->
            if (!isConnected) {
                Log.e(TAG, "Failed to connect to Play Store for purchase check")
                callback(false, null, null)
                return@connectPlayStore
            }

            // Check one-time purchases - both tiers are INAPP products
            val inappParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()

            billingClient.queryPurchasesAsync(inappParams) { billingResult, purchases ->
                Log.d(TAG, "queryPurchasesAsync INAPP callback: ${billingResult.responseCode}, ${purchases.size}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    var foundProduct: String? = null
                    var foundTimestamp: Long? = null

                    for (purchase in purchases) {
                        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            // Ensure purchase is acknowledged
                            if (!purchase.isAcknowledged) {
                                acknowledgePurchase(purchase)
                            }

                            val purchaseTime = purchase.purchaseTime
                            if (purchase.products.contains(SUPPORT_BASIC)) {
                                // prefer the latest timestamp
                                if (foundTimestamp == null || purchaseTime > foundTimestamp) {
                                    foundProduct = SUPPORT_BASIC
                                    foundTimestamp = purchaseTime
                                }
                            }
                            if (purchase.products.contains(SUPPORT_GENEROUS)) {
                                if (foundTimestamp == null || purchaseTime > foundTimestamp) {
                                    foundProduct = SUPPORT_GENEROUS
                                    foundTimestamp = purchaseTime
                                }
                            }
                        }
                    }

                    val hasPurchase = foundProduct != null
                    callback(hasPurchase, foundProduct, foundTimestamp)
                } else {
                    Log.e(TAG, "Failed to query inapp purchases: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    callback(false, null, null)
                }
            }
        }
    }


    fun fetchProductDetails(productIds: List<String>, callback: (List<PlanDetails>) -> Unit) {
        connectPlayStore { isConnected ->
            if (!isConnected) {
                callback(emptyList())
                return@connectPlayStore
            }

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    productIds.map { id ->
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(id)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    }
                )
                .build()

            billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val detailsList = queryProductDetailsResult.productDetailsList ?: emptyList()
                    val plans = detailsList.map { details ->
                        PlanDetails(
                            id = details.productId,
                            formattedPrice = details.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                        )
                    }
                    callback(plans)
                } else {
                    Log.e(TAG, "Failed to fetch product details: ${billingResult.responseCode}")
                    callback(emptyList())
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
                    
                    if (billingClient.isReady) {
                        val launchResult = billingClient.launchBillingFlow(activity, billingParamsBuilder)

                        if (launchResult.responseCode != BillingClient.BillingResponseCode.OK) {
                            Log.e(TAG, "Failed to launch billing flow: ${launchResult.responseCode} - ${launchResult.debugMessage}")
                            purchaseCallback = null
                            callback(false)
                        }
                    } else {
                        Log.e(TAG, "BillingClient is not ready right before launching flow")
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

                    if (!purchase.isAcknowledged) {
                        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                        
                        if (billingClient.isReady) {
                            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                    Log.d(TAG, "Purchase acknowledged successfully: ${purchase.products}")
                                    purchaseCallback?.invoke(true)
                                    purchaseCallback = null
                                } else {
                                    Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                                    // Still grant benefit if state is PURCHASED, even if acknowledgment failed (might succeed later or via restore)
                                    purchaseCallback?.invoke(true)
                                    purchaseCallback = null
                                }
                            }
                        } else {
                            Log.e(TAG, "BillingClient is not ready for acknowledgment")
                            purchaseCallback?.invoke(true)
                            purchaseCallback = null
                        }
                    } else {
                        Log.d(TAG, "Purchase already acknowledged: ${purchase.products}")
                        purchaseCallback?.invoke(true)
                        purchaseCallback = null
                    }
                }
                Purchase.PurchaseState.PENDING -> {
                    Log.d(TAG, "Purchase is pending: ${purchase.products}")
                    // Optionally notify user that purchase is pending
                    // Don't invoke callback yet - wait for final state
                }
                else -> {
                    Log.w(TAG, "Purchase state is unsuccessful (${purchase.purchaseState}): ${purchase.products}")
                    purchaseCallback?.invoke(false)
                    purchaseCallback = null
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged && purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            
            if (billingClient.isReady) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase background-acknowledged successfully: ${purchase.products}")
                    } else {
                        Log.e(TAG, "Failed to background-acknowledge purchase: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                    }
                }
            } else {
                Log.e(TAG, "BillingClient is not ready for background acknowledgment")
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
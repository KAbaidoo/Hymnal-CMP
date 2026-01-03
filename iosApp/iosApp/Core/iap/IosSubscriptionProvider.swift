//
//  IosSubscriptionProvider.swift
//  iosApp
//
//  Created by kobby on 25/12/2025.
//  Copyright © 2025 orgName. All rights reserved.

import ComposeApp
import Foundation
import FirebaseCrashlytics
import StoreKit
import UIKit

class IosSubscriptionProvider: NSObject, NativeSubscriptionProvider, SKProductsRequestDelegate, SKPaymentTransactionObserver {
    let YEARLY_SUBSCRIPTION_ID = "yearly_subscription"
    let ONETIME_PURCHASE_ID = "onetime_purchase"

    var products: [SKProduct] = []
    
    var purchaseCallBack: ((KotlinBoolean) -> Void)? = nil
    var restoreCallBack: ((KotlinBoolean) -> Void)? = nil

    override init() {
        super.init()
        SKPaymentQueue.default().add(self)
    }
    
    public func fetchSubscriptions() {
        let productIds: Set<String> = [YEARLY_SUBSCRIPTION_ID, ONETIME_PURCHASE_ID]
        let request = SKProductsRequest(productIdentifiers: productIds)
        request.delegate = self
        request.start()
    }
    
    public func productsRequest(_ request: SKProductsRequest, didReceive response: SKProductsResponse) {
        self.products = response.products
        print("productsRequest is called: \(self.products)")
        for product in products {
            print(product.productIdentifier)
        }
    }
    
    public func purchaseSubscription(productId: String, callback: @escaping (KotlinBoolean) -> Void) -> Bool {
        guard let product = products.first(where: { $0.productIdentifier == productId }) else {
            callback(KotlinBoolean(value: false))
            return false
        }
        let payment = SKPayment(product: product)
        purchaseCallBack = callback
        SKPaymentQueue.default().add(payment)
        return true
    }
    
    public func restorePurchases(callback: @escaping (KotlinBoolean) -> Void) {
        restoreCallBack = callback
        SKPaymentQueue.default().restoreCompletedTransactions()
    }
    
    public func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
        for transaction in transactions {
            switch transaction.transactionState {
            case .purchased:
                unlockContent(productId: transaction.payment.productIdentifier)
                SKPaymentQueue.default().finishTransaction(transaction)
            case .restored:
                unlockContent(productId: transaction.payment.productIdentifier)
                SKPaymentQueue.default().finishTransaction(transaction)
            case .failed:
                purchaseCallBack?(KotlinBoolean(value: false))
                SKPaymentQueue.default().finishTransaction(transaction)
            default:
                break
            }
        }
    }
    
    public func paymentQueueRestoreCompletedTransactionsFinished(_ queue: SKPaymentQueue) {
        // Check if any purchases were restored
        let yearlySubscribed = UserDefaults.standard.bool(forKey: YEARLY_SUBSCRIPTION_ID)
        let onetimePurchased = UserDefaults.standard.bool(forKey: ONETIME_PURCHASE_ID)
        
        let hasRestoredPurchases = yearlySubscribed || onetimePurchased
        restoreCallBack?(KotlinBoolean(value: hasRestoredPurchases))
        restoreCallBack = nil
    }
    
    public func paymentQueue(_ queue: SKPaymentQueue, restoreCompletedTransactionsFailedWithError error: Error) {
        print("Restore failed: \(error.localizedDescription)")
        restoreCallBack?(KotlinBoolean(value: false))
        restoreCallBack = nil
    }
    
    private func unlockContent(productId: String) {
        purchaseCallBack?(KotlinBoolean(value: true))
        UserDefaults.standard.set(true, forKey: productId)
    }
    
    public func isUserSubscribed(callback: @escaping (KotlinBoolean) -> Void) {
        let yearlySubscribed = UserDefaults.standard.bool(forKey: YEARLY_SUBSCRIPTION_ID)
        let onetimePurchased = UserDefaults.standard.bool(forKey: ONETIME_PURCHASE_ID)
        callback(KotlinBoolean(value: yearlySubscribed || onetimePurchased))
    }

    public func manageSubscription() {
        if let url = URL(string: "https://apps.apple.com/account/subscriptions") {
            UIApplication.shared.open(url)
        }
    }
}

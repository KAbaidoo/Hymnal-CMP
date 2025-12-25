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

class IosSubscriptionProvider: NativeSubscriptionProvider, NSObject,SKProductsRequestDelegate, SKPaymentTransactionObserver {
    let PRODUCT_ID = "ios_subscription"
    
        var products: [SKProduct] = []
        
        var purchaseCallBack:((Bool)->Void)? = nil
        
        override init() {
            super.init()
            SKPaymentQueue.default().add(self)
        }
        
        public func fetchSubscriptions() {
            let request = SKProductsRequest(productIdentifiers: [PRODUCT_ID])
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
        
       public func purchaseSubscription(callback: @escaping (Bool) -> Void) -> Bool {
            guard let product = products.first(where: { $0.productIdentifier == PRODUCT_ID }) else {
                callback(false)
                return false
            }
            let payment = SKPayment(product: product)
            purchaseCallBack = callback
            SKPaymentQueue.default().add(payment)
            return true
        }
        
       public func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
            for transaction in transactions {
                switch transaction.transactionState {
                case .purchased:
                    unlockContent(productId: transaction.payment.productIdentifier)
                    
                    SKPaymentQueue.default().finishTransaction(transaction)
                case .failed:
                    purchaseCallBack?(false)
                    SKPaymentQueue.default().finishTransaction(transaction)
                default:
                    break
                }
            }
        }
        
        private func unlockContent(productId: String) {
            purchaseCallBack?(true)
            UserDefaults.standard.set(true, forKey: productId)
        }
        
        public func isUserSubscribed(callback: @escaping (Bool) -> Void) {
            let isSubscribed = UserDefaults.standard.bool(forKey: PRODUCT_ID)
            callback(isSubscribed)
        }

        public func manageSubscription() {
            if let url = URL(string: "https://apps.apple.com/account/subscriptions") {
                UIApplication.shared.open(url)
            }
        }
}

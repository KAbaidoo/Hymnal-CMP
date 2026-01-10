//
//  IosPurchaseProvider.swift
//  iosApp
//
//  Created by kobby on 25/12/2025.
//  Copyright © 2025 orgName. All rights reserved.

import ComposeApp
import Foundation
import FirebaseCrashlytics
import StoreKit
import UIKit

class IosPurchaseProvider: NSObject, NativePurchaseProvider, SKProductsRequestDelegate, SKPaymentTransactionObserver {
    let SUPPORT_BASIC_ID = "support_basic"
    let SUPPORT_GENEROUS_ID = "support_generous"

    var products: [SKProduct] = []
    
    var purchaseCallBack: ((KotlinBoolean) -> Void)? = nil
    var restoreCallBack: ((KotlinBoolean) -> Void)? = nil
    // Collect restored products and timestamps
    private var restoredProducts: [(String, TimeInterval)] = []

    override init() {
        super.init()
        SKPaymentQueue.default().add(self)
    }
    
    public func fetchPurchases() {
        let productIds: Set<String> = [SUPPORT_BASIC_ID, SUPPORT_GENEROUS_ID]
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
    
    public func purchasePurchase(productId: String, callback: @escaping (KotlinBoolean) -> Void) -> Bool {
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
        // Clear previous restoredProducts to collect fresh ones
        restoredProducts.removeAll()
        SKPaymentQueue.default().restoreCompletedTransactions()
    }
    
    public func paymentQueue(_ queue: SKPaymentQueue, updatedTransactions transactions: [SKPaymentTransaction]) {
        for transaction in transactions {
            switch transaction.transactionState {
            case .purchased:
                unlockContent(productId: transaction.payment.productIdentifier, transactionDate: transaction.transactionDate)
                SKPaymentQueue.default().finishTransaction(transaction)
            case .restored:
                unlockContent(productId: transaction.payment.productIdentifier, transactionDate: transaction.transactionDate)
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
        let basicPurchased = UserDefaults.standard.bool(forKey: SUPPORT_BASIC_ID)
        let generousPurchased = UserDefaults.standard.bool(forKey: SUPPORT_GENEROUS_ID)

        let hasRestoredPurchases = basicPurchased || generousPurchased
        restoreCallBack?(KotlinBoolean(value: hasRestoredPurchases))
        restoreCallBack = nil
    }
    
    public func paymentQueue(_ queue: SKPaymentQueue, restoreCompletedTransactionsFailedWithError error: Error) {
        print("Restore failed: \(error.localizedDescription)")
        restoreCallBack?(KotlinBoolean(value: false))
        restoreCallBack = nil
    }
    
    private func unlockContent(productId: String, transactionDate: Date?) {
        purchaseCallBack?(KotlinBoolean(value: true))
        UserDefaults.standard.set(true, forKey: productId)
        // store purchase timestamp in millis
        let timestampMs = Int64((transactionDate ?? Date()).timeIntervalSince1970 * 1000.0)
        UserDefaults.standard.set(timestampMs, forKey: "\(productId)_purchaseDate")
        // keep restoredProducts in memory for immediate access
        // avoid duplicates
        if !restoredProducts.contains(where: { $0.0 == productId }) {
            restoredProducts.append((productId, Double(timestampMs)))
        }
    }
    
    public func isUserPurchased(callback: @escaping (KotlinBoolean) -> Void) {
        let basicPurchased = UserDefaults.standard.bool(forKey: SUPPORT_BASIC_ID)
        let generousPurchased = UserDefaults.standard.bool(forKey: SUPPORT_GENEROUS_ID)
        callback(KotlinBoolean(value: basicPurchased || generousPurchased))
    }

    // New: provide restored purchases info as a semicolon-separated string "productId,timestamp;productId,timestamp"
    public func getRestoredPurchasesInfo(callback: @escaping (KotlinString?) -> Void) {
        if restoredProducts.isEmpty {
            // attempt to read from UserDefaults if in-memory empty
            var entries: [String] = []
            for pid in [SUPPORT_BASIC_ID, SUPPORT_GENEROUS_ID] {
                if UserDefaults.standard.bool(forKey: pid) {
                    let ts = UserDefaults.standard.object(forKey: "\(pid)_purchaseDate") as? Int64 ?? Int64(Date().timeIntervalSince1970 * 1000.0)
                    entries.append("\(pid),\(ts)")
                }
            }
            if entries.isEmpty {
                callback(nil)
                return
            }
            let joined = entries.joined(separator: ";")
            callback(KotlinString(string: joined))
            return
        }

        let parts = restoredProducts.map { "\($0.0),\(Int64($0.1))" }
        let joined = parts.joined(separator: ";")
        callback(KotlinString(string: joined))
    }

    public func managePurchase() {
        if let url = URL(string: "https://apps.apple.com/account/subscriptions") {
            UIApplication.shared.open(url)
        }
    }
}

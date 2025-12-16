//
//  IosCrashlyticsProvider.swift
//  iosApp
//
//  Created by kobby on 28/11/2025.
//  Copyright © 2025 orgName. All rights reserved.

import ComposeApp
import Foundation
import FirebaseCrashlytics

class IosCrashlyticsProvider: NativeCrashlyticsProvider {
    func log(message: String) {
        Crashlytics.crashlytics().log(message)
    }
    
    func recordException(error: any Error) {
        // Fix: use the shared instance instead of unavailable init()
        Crashlytics.crashlytics().record(error: error)
    }
    
    func setCustomKey(key: String, value: Bool) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }
    
    func setCustomKey(key: String, value_ value: Int32) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }
    
    func setCustomKey(key: String, value__ value: String) {
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }
    
    func setUserId(userId: String) {
        Crashlytics.crashlytics().setUserID(userId)
    }
}

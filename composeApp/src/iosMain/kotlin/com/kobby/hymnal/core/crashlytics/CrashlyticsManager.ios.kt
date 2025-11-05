package com.kobby.hymnal.core.crashlytics

/**
 * iOS implementation of CrashlyticsManager
 * 
 * Note: Full Firebase Crashlytics integration on iOS requires:
 * 1. Adding Firebase Crashlytics via CocoaPods or SPM to the Xcode project
 * 2. Configuring the Run Script phase for dSYM upload
 * 3. Enabling Crashlytics in iOSApp.swift
 * 
 * This is a stub implementation that logs to console.
 * For production, integrate with Firebase SDK via CocoaPods interop.
 */
class IosCrashlyticsManager : CrashlyticsManager {
    
    init {
        println("IosCrashlyticsManager initialized - Stub implementation")
        println("To enable full Crashlytics on iOS:")
        println("1. Add Firebase/Crashlytics pod to your Podfile")
        println("2. Configure dSYM upload in Xcode build phases")
        println("3. Initialize Firebase in iOSApp.swift")
    }
    
    override fun recordException(throwable: Throwable) {
        // Stub: In production, call FIRCrashlytics.crashlytics().record(error)
        println("Crashlytics (iOS): Exception recorded - ${throwable.message}")
        println("Stack trace: ${throwable.stackTraceToString()}")
    }
    
    override fun setCustomKey(key: String, value: String) {
        // Stub: In production, call FIRCrashlytics.crashlytics().setCustomValue(value, forKey: key)
        println("Crashlytics (iOS): Custom key - $key: $value")
    }
    
    override fun setCustomKey(key: String, value: Boolean) {
        // Stub: In production, call FIRCrashlytics.crashlytics().setCustomValue(value, forKey: key)
        println("Crashlytics (iOS): Custom key - $key: $value")
    }
    
    override fun setCustomKey(key: String, value: Int) {
        // Stub: In production, call FIRCrashlytics.crashlytics().setCustomValue(value, forKey: key)
        println("Crashlytics (iOS): Custom key - $key: $value")
    }
    
    override fun setUserId(userId: String) {
        // Stub: In production, call FIRCrashlytics.crashlytics().setUserID(userId)
        println("Crashlytics (iOS): User ID set - $userId")
    }
    
    override fun log(message: String) {
        // Stub: In production, call FIRCrashlytics.crashlytics().log(message)
        println("Crashlytics (iOS): Log - $message")
    }
}

actual fun createCrashlyticsManager(): CrashlyticsManager = IosCrashlyticsManager()

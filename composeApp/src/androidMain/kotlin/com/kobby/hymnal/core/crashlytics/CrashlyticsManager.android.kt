package com.kobby.hymnal.core.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kobby.hymnal.BuildConfig

/**
 * Android implementation of CrashlyticsManager using Firebase Crashlytics
 */
class AndroidCrashlyticsManager : CrashlyticsManager {
    private val crashlytics: FirebaseCrashlytics = FirebaseCrashlytics.getInstance()
    
    init {
        // Only enable Crashlytics in release builds
        crashlytics.setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
    
    override fun recordException(throwable: Throwable) {
        if (!BuildConfig.DEBUG) {
            crashlytics.recordException(throwable)
        }
    }
    
    override fun setCustomKey(key: String, value: String) {
        if (!BuildConfig.DEBUG) {
            crashlytics.setCustomKey(key, value)
        }
    }
    
    override fun setCustomKey(key: String, value: Boolean) {
        if (!BuildConfig.DEBUG) {
            crashlytics.setCustomKey(key, value)
        }
    }
    
    override fun setCustomKey(key: String, value: Int) {
        if (!BuildConfig.DEBUG) {
            crashlytics.setCustomKey(key, value)
        }
    }
    
    override fun setUserId(userId: String) {
        if (!BuildConfig.DEBUG) {
            crashlytics.setUserId(userId)
        }
    }
    
    override fun log(message: String) {
        if (!BuildConfig.DEBUG) {
            crashlytics.log(message)
        }
    }
}

actual fun createCrashlyticsManager(): CrashlyticsManager = AndroidCrashlyticsManager()

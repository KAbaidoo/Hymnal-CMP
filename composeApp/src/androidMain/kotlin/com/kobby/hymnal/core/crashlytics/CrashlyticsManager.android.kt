package com.kobby.hymnal.core.crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kobby.hymnal.BuildConfig

/**
 * Android implementation of CrashlyticsManager using Firebase Crashlytics
 */
class AndroidCrashlyticsManager : CrashlyticsManager {
    // Lazily initialize Crashlytics only for release builds to avoid debug network calls
    private val crashlytics: FirebaseCrashlytics? = if (!BuildConfig.DEBUG) {
        FirebaseCrashlytics.getInstance().apply {
            // Ensure collection is enabled in release
            setCrashlyticsCollectionEnabled(true)
        }
    } else {
        null
    }
    
    override fun recordException(throwable: Throwable) {
        crashlytics?.recordException(throwable)
    }
    
    override fun setCustomKey(key: String, value: String) {
        crashlytics?.setCustomKey(key, value)
    }
    
    override fun setCustomKey(key: String, value: Boolean) {
        crashlytics?.setCustomKey(key, value)
    }
    
    override fun setCustomKey(key: String, value: Int) {
        crashlytics?.setCustomKey(key, value)
    }
    
    override fun setUserId(userId: String) {
        crashlytics?.setUserId(userId)
    }
    
    override fun log(message: String) {
        crashlytics?.log(message)
    }
}

actual fun createCrashlyticsManager(): CrashlyticsManager = AndroidCrashlyticsManager()

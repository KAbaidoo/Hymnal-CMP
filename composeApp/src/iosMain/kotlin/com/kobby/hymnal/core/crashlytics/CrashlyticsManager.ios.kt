package com.kobby.hymnal.core.crashlytics

import platform.Foundation.NSLog
import platform.Foundation.NSError

// NOTE:
// - When Firebase Crashlytics is added to the iOS app using Swift Package Manager (SPM),
//   the Firebase Swift APIs are added to the Xcode app target, not automatically
//   available to Kotlin/Native during compilation of the shared framework.
// - The code below provides a safe no-op implementation so the Kotlin build succeeds
//   when you use SPM. See the instructions below for how to call Crashlytics from
//   the iOS app using a Swift bridge (recommended) or how to keep CocoaPods if you
//   prefer direct Kotlin/Native interop.

class IosCrashlyticsManager : CrashlyticsManager {

    override fun recordException(throwable: Throwable) {
        // Safe fallback: log locally. Replace with a bridge call when you add the Swift wrapper.
        val domain = throwable::class.simpleName ?: "Unknown"
        val error = NSError.errorWithDomain(
            domain = domain,
            code = 0,
            userInfo = mapOf(
                "message" to (throwable.message ?: "No message"),
                "stackTrace" to (throwable.stackTraceToString())
            )
        )
        // If you implement a Swift bridge, call it here, e.g. CrashlyticsBridge.recordError(error)
        nativeCrashlyticsProvider?.recordException(error)
    }

    override fun setCustomKey(key: String, value: String) {
        nativeCrashlyticsProvider?.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        nativeCrashlyticsProvider?.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Int) {
        nativeCrashlyticsProvider?.setCustomKey(key, value)
    }

    override fun setUserId(userId: String) {
        nativeCrashlyticsProvider?.setUserId(userId)
    }

    override fun log(message: String) {
        nativeCrashlyticsProvider?.log(message)
    }
}

actual fun createCrashlyticsManager(): CrashlyticsManager = IosCrashlyticsManager()

interface NativeCrashlyticsProvider {
    fun recordException(error: NSError)
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Boolean)
    fun setCustomKey(key: String, value: Int)
    fun setUserId(userId: String)
    fun log(message: String)
}

private var nativeCrashlyticsProvider: NativeCrashlyticsProvider? = null

fun initializeNativeCrashlyticsProvider(provider: NativeCrashlyticsProvider) {
    nativeCrashlyticsProvider = provider
}

// ---------------------------------------------------------------------------
// How to wire real FirebaseCrashlytics when using SPM (recommended options):
//
// 1) Keep CocoaPods (previous approach)
//    - CocoaPods exposes the Objective-C API (FIRCrashlytics) to Kotlin/Native via
//      the CocoaPods Gradle integration; you can keep the original Kotlin iOS file.
//
// 2) Use SPM + Swift bridge in the iOS app (recommended for SPM users):
//    - In your Xcode app target (iosApp), add Firebase Crashlytics via SPM.
//    - Create a Swift file in the iosApp target, e.g. `CrashlyticsBridge.swift`,
//      and expose @objc static methods that call Crashlytics.crashlytics().
//    - Call the bridge from Swift and inject callbacks into shared Kotlin code,
//      or add an Objective-C compatible wrapper and cinterop if needed.
//
// 3) Expose SPM package to Kotlin/Native via a binary framework + cinterop (advanced).
// ---------------------------------------------------------------------------

package com.kobby.hymnal.core.crashlytics

import cocoapods.FirebaseCrashlytics.FIRCrashlytics
import platform.Foundation.NSError

class IosCrashlyticsManager : CrashlyticsManager {
    private val crashlytics = FIRCrashlytics.crashlytics()

    override fun recordException(throwable: Throwable) {
        val error = NSError.errorWithDomain(
            domain = throwable::class.simpleName ?: "Unknown",
            code = 0,
            userInfo = mapOf(
                "message" to (throwable.message ?: "No message"),
                "stackTrace" to (throwable.stackTraceToString())
            )
        )
        crashlytics.recordError(error)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomValue(value, key)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        crashlytics.setCustomValue(value, key)
    }

    override fun setCustomKey(key: String, value: Int) {
        crashlytics.setCustomValue(value.toLong(), key)
    }

    override fun setUserId(userId: String) {
        crashlytics.setUserID(userId)
    }

    override fun log(message: String) {
        crashlytics.log(message)
    }
}

actual fun createCrashlyticsManager(): CrashlyticsManager = IosCrashlyticsManager()

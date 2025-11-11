package com.kobby.hymnal.core.crashlytics

/**
 * Common interface for Crashlytics across platforms.
 * Provides crash reporting, logging, and custom key tracking.
 */
interface CrashlyticsManager {
    /**
     * Log a non-fatal exception to Crashlytics
     */
    fun recordException(throwable: Throwable)
    
    /**
     * Set a custom key-value pair for crash context
     */
    fun setCustomKey(key: String, value: String)
    
    /**
     * Set a custom key-value pair for crash context (Boolean)
     */
    fun setCustomKey(key: String, value: Boolean)
    
    /**
     * Set a custom key-value pair for crash context (Int)
     */
    fun setCustomKey(key: String, value: Int)
    
    /**
     * Set user identifier for crash reports
     */
    fun setUserId(userId: String)
    
    /**
     * Log a message for crash context
     */
    fun log(message: String)
}

/**
 * Expect declaration for platform-specific Crashlytics implementation
 */
expect fun createCrashlyticsManager(): CrashlyticsManager

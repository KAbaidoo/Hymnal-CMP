package com.kobby.hymnal.core.crashlytics

/**
 * Extension functions for easy Crashlytics integration
 */

/**
 * Execute a block and catch any exceptions, reporting them to Crashlytics
 * Returns the result or null if an exception occurred
 */
inline fun <T> CrashlyticsManager.safeLet(
    logMessage: String? = null,
    customKeys: Map<String, Any> = emptyMap(),
    block: () -> T
): T? {
    return try {
        block()
    } catch (e: Exception) {
        logMessage?.let { log(it) }
        customKeys.forEach { (key, value) ->
            when (value) {
                is String -> setCustomKey(key, value)
                is Boolean -> setCustomKey(key, value)
                is Int -> setCustomKey(key, value)
                else -> setCustomKey(key, value.toString())
            }
        }
        recordException(e)
        null
    }
}

/**
 * Execute a suspend block and catch any exceptions, reporting them to Crashlytics
 * Returns the result or null if an exception occurred
 * Note: Catches Exception, not Throwable. For fatal errors, they will propagate.
 */
suspend inline fun <T> CrashlyticsManager.safeCall(
    logMessage: String? = null,
    customKeys: Map<String, Any> = emptyMap(),
    crossinline block: suspend () -> T
): T? {
    return try {
        block()
    } catch (e: Exception) {
        logMessage?.let { log(it) }
        customKeys.forEach { (key, value) ->
            when (value) {
                is String -> setCustomKey(key, value)
                is Boolean -> setCustomKey(key, value)
                is Int -> setCustomKey(key, value)
                else -> setCustomKey(key, value.toString())
            }
        }
        recordException(e)
        null
    }
}

/**
 * Execute a block and catch any exceptions, reporting them to Crashlytics
 * Throws the exception after reporting
 */
inline fun <T> CrashlyticsManager.safeExecute(
    logMessage: String? = null,
    customKeys: Map<String, Any> = emptyMap(),
    block: () -> T
): T {
    return try {
        block()
    } catch (e: Exception) {
        logMessage?.let { log(it) }
        customKeys.forEach { (key, value) ->
            when (value) {
                is String -> setCustomKey(key, value)
                is Boolean -> setCustomKey(key, value)
                is Int -> setCustomKey(key, value)
                else -> setCustomKey(key, value.toString())
            }
        }
        recordException(e)
        throw e
    }
}

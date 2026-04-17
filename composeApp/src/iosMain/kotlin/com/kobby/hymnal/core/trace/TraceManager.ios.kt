package com.kobby.hymnal.core.trace

import com.kobby.hymnal.core.notifications.NotificationCategory
import platform.Foundation.NSLog

/**
 * iOS tracing implementation.
 * Delegates to a native provider injected from the Swift app target.
 */
class IosTraceManager : TraceManager {
    override fun track(event: String, params: Map<String, String>) {
        val safeEvent = TraceSanitizer.sanitizeEventName(event)
        val safeParams = TraceSanitizer.sanitizeParams(params)

        if (isTraceDebugLoggingEnabled) {
            // Avoid ObjC variadic formatting with Kotlin/Native values to prevent EXC_BAD_ACCESS.
            NSLog("TraceManager event=$safeEvent params=$safeParams")
        }

        nativeTraceProvider?.track(safeEvent, safeParams)
            ?: NSLog("TraceManager: missing native provider for event=$safeEvent")
    }
}

actual fun createTraceManager(): TraceManager = IosTraceManager()

interface NativeTraceProvider {
    fun track(event: String, params: Map<String, String>)
}

private var nativeTraceProvider: NativeTraceProvider? = null
private var isTraceDebugLoggingEnabled: Boolean = false

fun initializeNativeTraceProvider(
    provider: NativeTraceProvider,
    debugLoggingEnabled: Boolean = false
) {
    nativeTraceProvider = provider
    isTraceDebugLoggingEnabled = debugLoggingEnabled
}

fun trackIosNotificationReceived(
    category: String,
    messageId: String? = null,
    source: String? = null
) {
    val traceManager = createTraceManager()
    traceManager.track(
        TraceEvents.NOTIFICATION_RECEIVED,
        traceParams("category" to category)
    )

    if (category == NotificationCategory.CAMPAIGN.name.lowercase()) {
        traceManager.track(
            TraceEvents.CAMPAIGN_RECEIVED,
            traceParams(
                "message_id" to (messageId ?: "unknown"),
                "from" to (source ?: "unknown")
            )
        )
    }
}

fun trackIosNotificationOpened(
    category: String,
    notificationId: String? = null
) {
    createTraceManager().track(
        TraceEvents.NOTIFICATION_OPENED,
        traceParams(
            "category" to category,
            "notification_id" to notificationId
        )
    )
}

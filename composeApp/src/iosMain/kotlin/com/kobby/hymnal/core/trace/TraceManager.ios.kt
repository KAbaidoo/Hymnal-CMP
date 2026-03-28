package com.kobby.hymnal.core.trace

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

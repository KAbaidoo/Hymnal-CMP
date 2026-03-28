package com.kobby.hymnal.core.trace

/**
 * Shared tracing contract for user behavior instrumentation.
 *
 * Platform-specific implementations can map events to analytics backends.
 */
interface TraceManager {
    fun track(event: String, params: Map<String, String> = emptyMap())
}

/**
 * Expect declaration for platform-specific tracing implementation.
 */
expect fun createTraceManager(): TraceManager

package com.kobby.hymnal.core.performance

/**
 * Execute a block of code with performance tracing
 * @param traceName The name of the trace
 * @param block The code block to execute
 * @return The result of the block
 */
inline fun <T> PerformanceManager.trace(traceName: String, block: (Trace) -> T): T {
    val trace = startTrace(traceName)
    return try {
        block(trace)
    } finally {
        trace.stop()
    }
}

/**
 * Execute a suspend block of code with performance tracing
 * @param traceName The name of the trace
 * @param block The suspend code block to execute
 * @return The result of the block
 */
suspend inline fun <T> PerformanceManager.traceSuspend(traceName: String, crossinline block: suspend (Trace) -> T): T {
    val trace = startTrace(traceName)
    return try {
        block(trace)
    } finally {
        trace.stop()
    }
}

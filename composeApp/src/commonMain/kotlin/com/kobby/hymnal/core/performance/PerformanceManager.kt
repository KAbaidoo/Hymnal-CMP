package com.kobby.hymnal.core.performance

/**
 * Common interface for Performance Monitoring across platforms.
 * Provides custom traces to measure performance of specific code sections.
 */
interface PerformanceManager {
    /**
     * Start a custom trace with the given name
     * @param traceName The name of the trace
     * @return A Trace object that can be used to stop the trace and add metrics
     */
    fun startTrace(traceName: String): Trace
    
    /**
     * Put a custom attribute on all traces
     * @param attribute The name of the attribute
     * @param value The value of the attribute
     */
    fun putAttribute(attribute: String, value: String)
}

/**
 * Interface representing a performance trace
 */
interface Trace {
    /**
     * Stop the trace and record the duration
     */
    fun stop()
    
    /**
     * Add a custom metric to the trace
     * @param metricName The name of the metric
     * @param value The value of the metric
     */
    fun putMetric(metricName: String, value: Long)
    
    /**
     * Increment a metric by 1
     * @param metricName The name of the metric to increment
     */
    fun incrementMetric(metricName: String)
    
    /**
     * Add a custom attribute to the trace
     * @param attribute The name of the attribute
     * @param value The value of the attribute
     */
    fun putAttribute(attribute: String, value: String)
}

/**
 * Expect declaration for platform-specific Performance implementation
 */
expect fun createPerformanceManager(): PerformanceManager

package com.kobby.hymnal.core.performance

import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace as FirebaseTrace
import com.kobby.hymnal.BuildConfig

/**
 * Android implementation of PerformanceManager using Firebase Performance Monitoring
 */
class AndroidPerformanceManager : PerformanceManager {
    // Lazily initialize Performance only for release builds to avoid debug overhead
    private val performance: FirebasePerformance? = if (!BuildConfig.DEBUG) {
        FirebasePerformance.getInstance().apply {
            // Enable performance collection in release
            isPerformanceCollectionEnabled = true
        }
    } else {
        null
    }
    
    override fun startTrace(traceName: String): Trace {
        return if (performance != null) {
            AndroidTrace(performance.newTrace(traceName).apply { start() })
        } else {
            // Return a no-op trace for debug builds
            NoOpTrace()
        }
    }
    
    override fun putAttribute(attribute: String, value: String) {
        performance?.putAttribute(attribute, value)
    }
}

/**
 * Android implementation of Trace wrapping Firebase Trace
 */
class AndroidTrace(private val firebaseTrace: FirebaseTrace) : Trace {
    override fun stop() {
        firebaseTrace.stop()
    }
    
    override fun putMetric(metricName: String, value: Long) {
        firebaseTrace.putMetric(metricName, value)
    }
    
    override fun incrementMetric(metricName: String) {
        firebaseTrace.incrementMetric(metricName, 1)
    }
    
    override fun putAttribute(attribute: String, value: String) {
        firebaseTrace.putAttribute(attribute, value)
    }
}

/**
 * No-op implementation for debug builds
 */
class NoOpTrace : Trace {
    override fun stop() {}
    override fun putMetric(metricName: String, value: Long) {}
    override fun incrementMetric(metricName: String) {}
    override fun putAttribute(attribute: String, value: String) {}
}

actual fun createPerformanceManager(): PerformanceManager = AndroidPerformanceManager()

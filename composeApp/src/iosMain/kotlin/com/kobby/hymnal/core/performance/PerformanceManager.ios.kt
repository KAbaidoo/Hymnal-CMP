package com.kobby.hymnal.core.performance

/**
 * iOS implementation of PerformanceManager
 * 
 * Note: Full Firebase Performance integration on iOS requires:
 * 1. Adding Firebase Performance via CocoaPods or SPM to the Xcode project
 * 2. Configuring the Firebase SDK in iOSApp.swift
 * 
 * This is a stub implementation that logs to console.
 * For production, integrate with Firebase SDK via CocoaPods interop.
 */
class IosPerformanceManager : PerformanceManager {
    
    init {
        println("IosPerformanceManager initialized - Stub implementation")
        println("To enable full Performance Monitoring on iOS:")
        println("1. Add Firebase/Performance pod to your Podfile")
        println("2. Initialize Firebase in iOSApp.swift")
    }
    
    override fun startTrace(traceName: String): Trace {
        return IosTrace(traceName)
    }
    
    override fun putAttribute(attribute: String, value: String) {
        println("Performance (iOS): Global attribute - $attribute: $value")
    }
}

/**
 * iOS stub implementation of Trace
 */
class IosTrace(private val traceName: String) : Trace {
    private val startTime = kotlin.system.getTimeMillis()
    
    init {
        println("Performance (iOS): Trace started - $traceName")
    }
    
    override fun stop() {
        val duration = kotlin.system.getTimeMillis() - startTime
        println("Performance (iOS): Trace stopped - $traceName (${duration}ms)")
    }
    
    override fun putMetric(metricName: String, value: Long) {
        println("Performance (iOS): Metric - $traceName.$metricName = $value")
    }
    
    override fun incrementMetric(metricName: String) {
        println("Performance (iOS): Metric incremented - $traceName.$metricName")
    }
    
    override fun putAttribute(attribute: String, value: String) {
        println("Performance (iOS): Attribute - $traceName.$attribute = $value")
    }
}

actual fun createPerformanceManager(): PerformanceManager = IosPerformanceManager()

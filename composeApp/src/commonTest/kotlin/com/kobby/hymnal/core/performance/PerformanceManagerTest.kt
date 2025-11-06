package com.kobby.hymnal.core.performance

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Test implementation of PerformanceManager for testing
 */
class TestPerformanceManager : PerformanceManager {
    val traces = mutableListOf<TestTrace>()
    val attributes = mutableMapOf<String, String>()
    
    override fun startTrace(traceName: String): Trace {
        val trace = TestTrace(traceName)
        traces.add(trace)
        return trace
    }
    
    override fun putAttribute(attribute: String, value: String) {
        attributes[attribute] = value
    }
}

/**
 * Test implementation of Trace
 */
class TestTrace(val name: String) : Trace {
    var isStopped = false
    val metrics = mutableMapOf<String, Long>()
    val attributes = mutableMapOf<String, String>()
    
    override fun stop() {
        isStopped = true
    }
    
    override fun putMetric(metricName: String, value: Long) {
        metrics[metricName] = value
    }
    
    override fun incrementMetric(metricName: String) {
        metrics[metricName] = (metrics[metricName] ?: 0) + 1
    }
    
    override fun putAttribute(attribute: String, value: String) {
        attributes[attribute] = value
    }
}

class PerformanceManagerTest {
    
    @Test
    fun testStartTrace() {
        val manager = TestPerformanceManager()
        val trace = manager.startTrace("test_trace")
        
        assertNotNull(trace)
        assertEquals(1, manager.traces.size)
        assertEquals("test_trace", manager.traces[0].name)
    }
    
    @Test
    fun testStopTrace() {
        val manager = TestPerformanceManager()
        val trace = manager.startTrace("test_trace") as TestTrace
        
        assertEquals(false, trace.isStopped)
        trace.stop()
        assertEquals(true, trace.isStopped)
    }
    
    @Test
    fun testPutMetric() {
        val manager = TestPerformanceManager()
        val trace = manager.startTrace("test_trace") as TestTrace
        
        trace.putMetric("test_metric", 42L)
        assertEquals(42L, trace.metrics["test_metric"])
    }
    
    @Test
    fun testIncrementMetric() {
        val manager = TestPerformanceManager()
        val trace = manager.startTrace("test_trace") as TestTrace
        
        trace.incrementMetric("counter")
        trace.incrementMetric("counter")
        trace.incrementMetric("counter")
        
        assertEquals(3L, trace.metrics["counter"])
    }
    
    @Test
    fun testPutAttribute() {
        val manager = TestPerformanceManager()
        val trace = manager.startTrace("test_trace") as TestTrace
        
        trace.putAttribute("key", "value")
        assertEquals("value", trace.attributes["key"])
    }
    
    @Test
    fun testGlobalAttribute() {
        val manager = TestPerformanceManager()
        
        manager.putAttribute("app_version", "1.0.0")
        assertEquals("1.0.0", manager.attributes["app_version"])
    }
    
    @Test
    fun testTraceExtension() {
        val manager = TestPerformanceManager()
        
        val result = manager.trace("test_operation") { trace ->
            trace.putMetric("items", 5L)
            "success"
        }
        
        assertEquals("success", result)
        assertEquals(1, manager.traces.size)
        assertEquals(true, manager.traces[0].isStopped)
        assertEquals(5L, manager.traces[0].metrics["items"])
    }
}

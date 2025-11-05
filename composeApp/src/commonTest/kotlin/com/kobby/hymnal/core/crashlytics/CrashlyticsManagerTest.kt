package com.kobby.hymnal.core.crashlytics

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Test for CrashlyticsManager functionality
 */
class CrashlyticsManagerTest {
    
    private class TestCrashlyticsManager : CrashlyticsManager {
        val exceptions = mutableListOf<Throwable>()
        val customKeys = mutableMapOf<String, Any>()
        val logs = mutableListOf<String>()
        var userId: String? = null
        
        override fun recordException(throwable: Throwable) {
            exceptions.add(throwable)
        }
        
        override fun setCustomKey(key: String, value: String) {
            customKeys[key] = value
        }
        
        override fun setCustomKey(key: String, value: Boolean) {
            customKeys[key] = value
        }
        
        override fun setCustomKey(key: String, value: Int) {
            customKeys[key] = value
        }
        
        override fun setUserId(userId: String) {
            this.userId = userId
        }
        
        override fun log(message: String) {
            logs.add(message)
        }
    }
    
    @Test
    fun testRecordException() {
        val manager = TestCrashlyticsManager()
        val exception = RuntimeException("Test exception")
        
        manager.recordException(exception)
        
        assertEquals(1, manager.exceptions.size)
        assertEquals(exception, manager.exceptions[0])
    }
    
    @Test
    fun testSetCustomKeyString() {
        val manager = TestCrashlyticsManager()
        
        manager.setCustomKey("key1", "value1")
        
        assertEquals("value1", manager.customKeys["key1"])
    }
    
    @Test
    fun testSetCustomKeyBoolean() {
        val manager = TestCrashlyticsManager()
        
        manager.setCustomKey("enabled", true)
        
        assertEquals(true, manager.customKeys["enabled"])
    }
    
    @Test
    fun testSetCustomKeyInt() {
        val manager = TestCrashlyticsManager()
        
        manager.setCustomKey("count", 42)
        
        assertEquals(42, manager.customKeys["count"])
    }
    
    @Test
    fun testSetUserId() {
        val manager = TestCrashlyticsManager()
        
        manager.setUserId("user123")
        
        assertEquals("user123", manager.userId)
    }
    
    @Test
    fun testLog() {
        val manager = TestCrashlyticsManager()
        
        manager.log("Test message")
        
        assertEquals(1, manager.logs.size)
        assertEquals("Test message", manager.logs[0])
    }
    
    @Test
    fun testMultipleOperations() {
        val manager = TestCrashlyticsManager()
        
        manager.setCustomKey("version", "1.0.0")
        manager.setCustomKey("debug", false)
        manager.log("Starting operation")
        manager.recordException(RuntimeException("Error"))
        manager.setUserId("user456")
        
        assertEquals(1, manager.exceptions.size)
        assertEquals(2, manager.customKeys.size)
        assertEquals(1, manager.logs.size)
        assertNotNull(manager.userId)
    }
    
    @Test
    fun testSafeLetSuccess() {
        val manager = TestCrashlyticsManager()
        
        val result = manager.safeLet {
            "success"
        }
        
        assertEquals("success", result)
        assertEquals(0, manager.exceptions.size)
    }
    
    @Test
    fun testSafeLetFailure() {
        val manager = TestCrashlyticsManager()
        
        val result = manager.safeLet(
            logMessage = "Operation failed",
            customKeys = mapOf("operation" to "test")
        ) {
            throw RuntimeException("Test error")
        }
        
        assertEquals(null, result)
        assertEquals(1, manager.exceptions.size)
        assertEquals(1, manager.logs.size)
        assertEquals("Operation failed", manager.logs[0])
        assertEquals("test", manager.customKeys["operation"])
    }
    
    @Test
    fun testSafeExecuteSuccess() {
        val manager = TestCrashlyticsManager()
        
        val result = manager.safeExecute {
            42
        }
        
        assertEquals(42, result)
        assertEquals(0, manager.exceptions.size)
    }
    
    @Test
    fun testSafeExecuteFailure() {
        val manager = TestCrashlyticsManager()
        
        try {
            manager.safeExecute(
                logMessage = "Critical error",
                customKeys = mapOf("critical" to true)
            ) {
                throw IllegalStateException("State error")
            }
            assertTrue(false, "Should have thrown exception")
        } catch (e: IllegalStateException) {
            assertEquals(1, manager.exceptions.size)
            assertEquals(1, manager.logs.size)
            assertEquals("Critical error", manager.logs[0])
            assertEquals(true, manager.customKeys["critical"])
        }
    }
}

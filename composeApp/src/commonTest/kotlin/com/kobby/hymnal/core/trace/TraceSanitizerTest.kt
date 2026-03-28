package com.kobby.hymnal.core.trace

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TraceSanitizerTest {

    @Test
    fun sanitizeEventName_normalizesAndLowercases() {
        val result = TraceSanitizer.sanitizeEventName("Home Navigation Click!")
        assertEquals("home_navigation_click", result)
    }

    @Test
    fun sanitizeEventName_fallsBackWhenBlank() {
        val result = TraceSanitizer.sanitizeEventName("   ")
        assertEquals("unknown_event", result)
    }

    @Test
    fun sanitizeParams_blocksRawQueryAndContentText() {
        val params = mapOf(
            "query" to "amazing grace",
            "search_query" to "hymn 12",
            "content" to "full hymn text here",
            "query_length" to "12",
            "hymn_id" to "44"
        )

        val result = TraceSanitizer.sanitizeParams(params)

        assertFalse(result.containsKey("query"))
        assertFalse(result.containsKey("search_query"))
        assertFalse(result.containsKey("content"))
        assertEquals("12", result["query_length"])
        assertEquals("44", result["hymn_id"])
    }

    @Test
    fun sanitizeParams_normalizesKeysAndTrimsValues() {
        val params = mapOf(
            "Entry Source" to "  more_menu  ",
            "Plan-Name" to "SupportGenerous"
        )

        val result = TraceSanitizer.sanitizeParams(params)

        assertEquals("more_menu", result["entry_source"])
        assertEquals("SupportGenerous", result["plan_name"])
    }

    @Test
    fun sanitizeParams_limitsParamCount() {
        val input = (1..40).associate { i -> "key_$i" to "v$i" }
        val result = TraceSanitizer.sanitizeParams(input)
        assertTrue(result.size <= 25)
    }
}


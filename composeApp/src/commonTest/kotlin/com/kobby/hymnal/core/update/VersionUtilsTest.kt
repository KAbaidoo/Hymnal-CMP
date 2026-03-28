package com.kobby.hymnal.core.update

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionUtilsTest {

    @Test
    fun testCompareVersions() {
        assertEquals(0, VersionUtils.compareVersions("1.0.0", "1.0.0"))
        assertEquals(-1, VersionUtils.compareVersions("1.0.0", "1.0.1"))
        assertEquals(1, VersionUtils.compareVersions("1.1.0", "1.0.1"))
        assertEquals(-1, VersionUtils.compareVersions("0.9.11", "0.10.0"))
        assertEquals(1, VersionUtils.compareVersions("1.0.0", "0.9.9"))
        assertEquals(0, VersionUtils.compareVersions("1.2", "1.2.0"))
        assertEquals(-1, VersionUtils.compareVersions("1.2", "1.2.1"))
    }

    @Test
    fun testIsUpdateAvailable() {
        assertTrue(VersionUtils.isUpdateAvailable("1.0.0", "1.0.1"))
        assertTrue(VersionUtils.isUpdateAvailable("0.9.11", "1.0.0"))
        assertFalse(VersionUtils.isUpdateAvailable("1.0.0", "1.0.0"))
        assertFalse(VersionUtils.isUpdateAvailable("1.1.0", "1.0.1"))
    }
}

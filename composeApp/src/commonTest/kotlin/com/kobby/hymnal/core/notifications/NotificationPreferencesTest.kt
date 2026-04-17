package com.kobby.hymnal.core.notifications

import com.russhwolf.settings.MapSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationPreferencesTest {

    @Test
    fun `defaults are enabled`() {
        val preferences = NotificationPreferences(MapSettings())
        val settings = preferences.notificationSettings.value

        assertTrue(settings.enabled)
    }

    @Test
    fun `updating enabled and last active persists in flow`() {
        val preferences = NotificationPreferences(MapSettings())

        preferences.setEnabled(false)
        preferences.markAppActive(1234L)

        val settings = preferences.notificationSettings.value
        kotlin.test.assertFalse(settings.enabled)
        assertEquals(1234L, settings.lastActiveTimestampMs)
    }
}

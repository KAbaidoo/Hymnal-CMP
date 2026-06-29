package com.kobby.hymnal.core.notifications

import com.kobby.hymnal.core.config.RemoteConfigManager
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.russhwolf.settings.MapSettings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotificationSchedulerTest {

    @Test
    fun `syncSchedules when disabled cancels notifications and still syncs campaign`() = runTest {
        val manager = FakeNotificationManager()
        val preferences = NotificationPreferences(MapSettings()).apply {
            setEnabled(false)
        }
        val config = FakeRemoteConfigManager()
        val trace = FakeTraceManager()
        val scheduler = NotificationScheduler(manager, preferences, config, trace, this)

        scheduler.syncSchedules()

        assertEquals(1, manager.cancelAllCalls)
        assertEquals(1, manager.syncCampaignSubscriptionCalls)
        assertEquals(0, manager.scheduleWeeklyCalls)
        assertEquals(0, manager.scheduleInactivityCalls)
        assertEquals(0, manager.scheduleSeasonalCalls)

        assertEquals(1, trace.events.size)
        assertEquals(TraceEvents.NOTIFICATION_SCHEDULED, trace.events[0].event)
        assertEquals("cancel_all", trace.events[0].params["action"])
        assertEquals("notifications_disabled", trace.events[0].params["reason"])
    }

    @Test
    fun `syncSchedules when enabled schedules all categories and records schedule traces`() = runTest {
        val manager = FakeNotificationManager()
        val preferences = NotificationPreferences(MapSettings()).apply {
            setEnabled(true)
        }
        val config = FakeRemoteConfigManager()
        val trace = FakeTraceManager()
        val scheduler = NotificationScheduler(manager, preferences, config, trace, this)

        scheduler.syncSchedules()

        assertEquals(0, manager.cancelAllCalls)
        assertEquals(1, manager.scheduleInactivityCalls)
        assertEquals(1, manager.scheduleWeeklyCalls)
        assertEquals(1, manager.scheduleSeasonalCalls)
        assertEquals(1, manager.syncCampaignSubscriptionCalls)

        val scheduleEvents = trace.events.filter { it.event == TraceEvents.NOTIFICATION_SCHEDULED }
        assertEquals(1, scheduleEvents.size) // Now only one "synced_all" trace on successful sync
        assertEquals("synced_all", scheduleEvents[0].params["action"])
    }

    @Test
    fun `onAppLaunched requests permission and marks app active`() = runTest {
        val manager = FakeNotificationManager()
        val preferences = NotificationPreferences(MapSettings())
        val config = FakeRemoteConfigManager()
        val trace = FakeTraceManager()
        val scheduler = NotificationScheduler(manager, preferences, config, trace, this)

        scheduler.onAppLaunched()

        assertEquals(1, manager.requestPermissionCalls)
        assertTrue(preferences.notificationSettings.value.lastActiveTimestampMs > 0L)
    }
}

private class FakeNotificationManager : NotificationManager {
    var scheduleWeeklyCalls = 0
    var scheduleInactivityCalls = 0
    var scheduleSeasonalCalls = 0
    var syncCampaignSubscriptionCalls = 0
    var cancelAllCalls = 0
    var requestPermissionCalls = 0
    var sendTestNotificationCalls = 0

    override fun scheduleWeekly(settings: NotificationSettings) {
        scheduleWeeklyCalls++
    }

    override fun scheduleInactivity(settings: NotificationSettings) {
        scheduleInactivityCalls++
    }

    override fun scheduleSeasonal(settings: NotificationSettings) {
        scheduleSeasonalCalls++
    }

    override fun syncCampaignSubscription() {
        syncCampaignSubscriptionCalls++
    }

    override fun cancelAll() {
        cancelAllCalls++
    }

    override fun requestPermission() {
        requestPermissionCalls++
    }

    override fun sendTestNotification() {
        sendTestNotificationCalls++
    }
}

private class FakeRemoteConfigManager : RemoteConfigManager {
    var fetchCalls = 0
    var activated = true

    override suspend fun fetchAndActivate(): Boolean {
        fetchCalls++
        return activated
    }

    override fun getString(key: String, defaultValue: String): String = defaultValue
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = defaultValue
    override fun getLong(key: String, defaultValue: Long): Long = defaultValue
}

private class FakeTraceManager : TraceManager {
    data class TraceRecord(
        val event: String,
        val params: Map<String, String>
    )

    val events = mutableListOf<TraceRecord>()

    override fun track(event: String, params: Map<String, String>) {
        events += TraceRecord(event = event, params = params)
    }
}

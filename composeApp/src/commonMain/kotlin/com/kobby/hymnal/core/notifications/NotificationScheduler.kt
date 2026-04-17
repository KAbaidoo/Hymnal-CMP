package com.kobby.hymnal.core.notifications

import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams

class NotificationScheduler(
    private val notificationManager: NotificationManager,
    private val preferences: NotificationPreferences,
    private val traceManager: TraceManager
) {

    private var hasSyncedThisSession = false

    fun onAppLaunched() {
        preferences.markAppActive()
        notificationManager.requestPermission()
        syncSchedules()
    }

    fun onAppBecameActive() {
        preferences.markAppActive()
        syncSchedules()
    }

    fun syncSchedules() {
        val settings = preferences.notificationSettings.value
        if (!settings.enabled) {
            notificationManager.cancelAll()
            notificationManager.syncCampaignSubscription()
            if (!hasSyncedThisSession) {
                traceManager.track(
                    TraceEvents.NOTIFICATION_SCHEDULED,
                    traceParams("action" to "cancel_all", "reason" to "notifications_disabled")
                )
                hasSyncedThisSession = true
            }
            return
        }

        notificationManager.scheduleInactivity()
        notificationManager.scheduleWeekly()
        notificationManager.scheduleSeasonal()
        notificationManager.syncCampaignSubscription()

        if (!hasSyncedThisSession) {
            traceManager.track(
                TraceEvents.NOTIFICATION_SCHEDULED,
                traceParams("action" to "synced_all")
            )
            hasSyncedThisSession = true
        }
    }
}

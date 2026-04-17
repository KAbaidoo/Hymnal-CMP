package com.kobby.hymnal.core.notifications

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams

class NotificationScheduler(
    private val notificationManager: NotificationManager,
    private val preferences: NotificationPreferences,
    private val traceManager: TraceManager
) {

    constructor() : this(
        notificationManager = object : KoinComponent {
            val value: NotificationManager by inject()
        }.value,
        preferences = object : KoinComponent {
            val value: NotificationPreferences by inject()
        }.value,
        traceManager = object : KoinComponent {
            val value: TraceManager by inject()
        }.value
    )

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
            traceManager.track(
                TraceEvents.NOTIFICATION_SCHEDULED,
                traceParams("action" to "cancel_all", "reason" to "notifications_disabled")
            )
            return
        }

        notificationManager.scheduleInactivity()
        traceManager.track(
            TraceEvents.NOTIFICATION_SCHEDULED,
            traceParams("category" to NotificationCategory.INACTIVITY.name.lowercase())
        )
        notificationManager.scheduleWeekly()
        traceManager.track(
            TraceEvents.NOTIFICATION_SCHEDULED,
            traceParams("category" to NotificationCategory.WEEKLY.name.lowercase())
        )
        notificationManager.scheduleSeasonal()
        traceManager.track(
            TraceEvents.NOTIFICATION_SCHEDULED,
            traceParams("category" to NotificationCategory.SEASONAL.name.lowercase())
        )
        notificationManager.syncCampaignSubscription()
    }
}

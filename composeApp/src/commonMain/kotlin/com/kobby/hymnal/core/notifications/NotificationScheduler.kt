package com.kobby.hymnal.core.notifications

import com.kobby.hymnal.core.config.RemoteConfigManager
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationScheduler(
    private val notificationManager: NotificationManager,
    private val preferences: NotificationPreferences,
    private val remoteConfigManager: RemoteConfigManager,
    private val traceManager: TraceManager
) {

    private var hasSyncedThisSession = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun onAppLaunched() {
        preferences.markAppActive()
        notificationManager.requestPermission()
        fetchAndSync()
    }

    fun onAppBecameActive() {
        preferences.markAppActive()
        fetchAndSync()
    }

    private fun fetchAndSync() {
        scope.launch {
            val activated = remoteConfigManager.fetchAndActivate()
            if (activated) {
                preferences.updateRemoteConfig(
                    weeklyEnabled = remoteConfigManager.getBoolean(
                        RemoteConfigManager.KEY_WEEKLY_ENABLED,
                        true
                    ),
                    weeklyTitle = remoteConfigManager.getString(
                        RemoteConfigManager.KEY_WEEKLY_TITLE,
                        NotificationDefaults.WEEKLY_TITLE
                    ),
                    weeklyBody = remoteConfigManager.getString(
                        RemoteConfigManager.KEY_WEEKLY_BODY,
                        NotificationDefaults.WEEKLY_BODY
                    ),
                    inactivityEnabled = remoteConfigManager.getBoolean(
                        RemoteConfigManager.KEY_INACTIVITY_ENABLED,
                        true
                    ),
                    inactivityTitle = remoteConfigManager.getString(
                        RemoteConfigManager.KEY_INACTIVITY_TITLE,
                        NotificationDefaults.NUDGE_TITLE
                    ),
                    inactivityBody = remoteConfigManager.getString(
                        RemoteConfigManager.KEY_INACTIVITY_BODY,
                        NotificationDefaults.NUDGE_BODY
                    ),
                    inactivityDays = remoteConfigManager.getLong(
                        RemoteConfigManager.KEY_INACTIVITY_DAYS,
                        NotificationDefaults.INACTIVITY_DAYS.toLong()
                    ).toInt(),
                    seasonalEnabled = remoteConfigManager.getBoolean(
                        RemoteConfigManager.KEY_SEASONAL_ENABLED,
                        true
                    )
                )
            }
            syncSchedules()
        }
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

        notificationManager.scheduleInactivity(settings)
        notificationManager.scheduleWeekly(settings)
        notificationManager.scheduleSeasonal(settings)
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

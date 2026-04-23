package com.kobby.hymnal.core.notifications

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

data class NotificationSettings(
    val enabled: Boolean = true,
    val lastActiveTimestampMs: Long = 0L,
    
    // Remote Config Managed values
    val weeklyEnabled: Boolean = true,
    val weeklyTitle: String = NotificationDefaults.WEEKLY_TITLE,
    val weeklyBody: String = NotificationDefaults.WEEKLY_BODY,
    
    val inactivityEnabled: Boolean = true,
    val inactivityTitle: String = NotificationDefaults.NUDGE_TITLE,
    val inactivityBody: String = NotificationDefaults.NUDGE_BODY,
    val inactivityDays: Int = NotificationDefaults.INACTIVITY_DAYS,
    
    val seasonalEnabled: Boolean = true
)

class NotificationPreferences(private val settings: Settings) {

    private companion object {
        const val KEY_ENABLED = "notifications_enabled"
        const val KEY_LAST_ACTIVE_TIMESTAMP = "notifications_last_active_timestamp"
        
        const val KEY_WEEKLY_ENABLED = "remote_notifications_weekly_enabled"
        const val KEY_WEEKLY_TITLE = "remote_notifications_weekly_title"
        const val KEY_WEEKLY_BODY = "remote_notifications_weekly_body"
        
        const val KEY_INACTIVITY_ENABLED = "remote_notifications_inactivity_enabled"
        const val KEY_INACTIVITY_TITLE = "remote_notifications_inactivity_title"
        const val KEY_INACTIVITY_BODY = "remote_notifications_inactivity_body"
        const val KEY_INACTIVITY_DAYS = "remote_notifications_inactivity_days"
        
        const val KEY_SEASONAL_ENABLED = "remote_notifications_seasonal_enabled"
    }

    private val _notificationSettings = MutableStateFlow(readSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    fun setEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_ENABLED, enabled)
        updateSettings()
    }

    fun markAppActive(nowMs: Long = Clock.System.now().toEpochMilliseconds()) {
        settings.putLong(KEY_LAST_ACTIVE_TIMESTAMP, nowMs)
        updateSettings()
    }

    fun updateRemoteConfig(
        weeklyEnabled: Boolean,
        weeklyTitle: String,
        weeklyBody: String,
        inactivityEnabled: Boolean,
        inactivityTitle: String,
        inactivityBody: String,
        inactivityDays: Int,
        seasonalEnabled: Boolean
    ) {
        settings.putBoolean(KEY_WEEKLY_ENABLED, weeklyEnabled)
        settings.putString(KEY_WEEKLY_TITLE, weeklyTitle)
        settings.putString(KEY_WEEKLY_BODY, weeklyBody)
        settings.putBoolean(KEY_INACTIVITY_ENABLED, inactivityEnabled)
        settings.putString(KEY_INACTIVITY_TITLE, inactivityTitle)
        settings.putString(KEY_INACTIVITY_BODY, inactivityBody)
        settings.putInt(KEY_INACTIVITY_DAYS, inactivityDays)
        settings.putBoolean(KEY_SEASONAL_ENABLED, seasonalEnabled)
        updateSettings()
    }

    private fun updateSettings() {
        _notificationSettings.value = readSettings()
    }

    private fun readSettings(): NotificationSettings {
        return NotificationSettings(
            enabled = settings.getBoolean(KEY_ENABLED, true),
            lastActiveTimestampMs = settings.getLong(KEY_LAST_ACTIVE_TIMESTAMP, 0L),
            weeklyEnabled = settings.getBoolean(KEY_WEEKLY_ENABLED, true),
            weeklyTitle = settings.getString(KEY_WEEKLY_TITLE, NotificationDefaults.WEEKLY_TITLE),
            weeklyBody = settings.getString(KEY_WEEKLY_BODY, NotificationDefaults.WEEKLY_BODY),
            inactivityEnabled = settings.getBoolean(KEY_INACTIVITY_ENABLED, true),
            inactivityTitle = settings.getString(KEY_INACTIVITY_TITLE, NotificationDefaults.NUDGE_TITLE),
            inactivityBody = settings.getString(KEY_INACTIVITY_BODY, NotificationDefaults.NUDGE_BODY),
            inactivityDays = settings.getInt(KEY_INACTIVITY_DAYS, NotificationDefaults.INACTIVITY_DAYS),
            seasonalEnabled = settings.getBoolean(KEY_SEASONAL_ENABLED, true)
        )
    }
}

package com.kobby.hymnal.core.notifications

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

data class NotificationSettings(
    val enabled: Boolean = true,
    val lastActiveTimestampMs: Long = 0L
)

class NotificationPreferences(private val settings: Settings) {

    private companion object {
        const val KEY_ENABLED = "notifications_enabled"
        const val KEY_LAST_ACTIVE_TIMESTAMP = "notifications_last_active_timestamp"
    }

    private val _notificationSettings = MutableStateFlow(readSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    fun setEnabled(enabled: Boolean) {
        settings.putBoolean(KEY_ENABLED, enabled)
        _notificationSettings.value = _notificationSettings.value.copy(enabled = enabled)
    }

    fun markAppActive(nowMs: Long = Clock.System.now().toEpochMilliseconds()) {
        settings.putLong(KEY_LAST_ACTIVE_TIMESTAMP, nowMs)
        _notificationSettings.value = _notificationSettings.value.copy(lastActiveTimestampMs = nowMs)
    }

    private fun readSettings(): NotificationSettings {
        return NotificationSettings(
            enabled = settings.getBoolean(KEY_ENABLED, true),
            lastActiveTimestampMs = settings.getLong(KEY_LAST_ACTIVE_TIMESTAMP, 0L)
        )
    }
}

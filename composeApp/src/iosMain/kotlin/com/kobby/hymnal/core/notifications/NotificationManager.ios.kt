package com.kobby.hymnal.core.notifications

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface NativeNotificationProvider {
    fun scheduleWeekly(settings: NotificationSettings)
    fun scheduleInactivity(settings: NotificationSettings)
    fun scheduleSeasonal(settings: NotificationSettings)
    fun syncCampaignSubscription(enabled: Boolean)
    fun cancelAll()
    fun requestPermission()
    fun sendTestNotification()
}

private var nativeNotificationProvider: NativeNotificationProvider? = null

fun initializeNativeNotificationProvider(provider: NativeNotificationProvider) {
    nativeNotificationProvider = provider
}

class IosNotificationManagerImpl(private val preferences: NotificationPreferences) : NotificationManager {

    override fun scheduleWeekly(settings: NotificationSettings) {
        if (!settings.enabled || !settings.weeklyEnabled) return
        nativeNotificationProvider?.scheduleWeekly(settings)
    }

    override fun scheduleInactivity(settings: NotificationSettings) {
        if (!settings.enabled || !settings.inactivityEnabled) return
        nativeNotificationProvider?.scheduleInactivity(settings)
    }

    override fun scheduleSeasonal(settings: NotificationSettings) {
        if (!settings.enabled || !settings.seasonalEnabled) return
        nativeNotificationProvider?.scheduleSeasonal(settings)
    }

    override fun syncCampaignSubscription() {
        val settings = preferences.notificationSettings.value
        nativeNotificationProvider?.syncCampaignSubscription(enabled = settings.enabled)
    }

    override fun cancelAll() {
        nativeNotificationProvider?.cancelAll()
    }

    override fun requestPermission() {
        nativeNotificationProvider?.requestPermission()
    }

    override fun sendTestNotification() {
        nativeNotificationProvider?.sendTestNotification()
    }
}

actual fun createNotificationManager(): NotificationManager {
    return object : KoinComponent {
        val preferences: NotificationPreferences by inject()
        val manager = IosNotificationManagerImpl(preferences)
    }.manager
}

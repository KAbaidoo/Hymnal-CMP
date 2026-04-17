package com.kobby.hymnal.core.notifications

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface NativeNotificationProvider {
    fun scheduleWeekly()
    fun scheduleInactivity()
    fun scheduleSeasonal(events: List<SeasonalNotificationEvent>)
    fun syncCampaignSubscription(enabled: Boolean)
    fun cancelAll()
    fun requestPermission()
}

private var nativeNotificationProvider: NativeNotificationProvider? = null

fun initializeNativeNotificationProvider(provider: NativeNotificationProvider) {
    nativeNotificationProvider = provider
}

class IosNotificationManagerImpl(private val preferences: NotificationPreferences) : NotificationManager {

    override fun scheduleWeekly() {
        val settings = preferences.notificationSettings.value
        if (!settings.enabled) return
        nativeNotificationProvider?.scheduleWeekly()
    }

    override fun scheduleInactivity() {
        val settings = preferences.notificationSettings.value
        if (!settings.enabled) return
        nativeNotificationProvider?.scheduleInactivity()
    }

    override fun scheduleSeasonal() {
        val settings = preferences.notificationSettings.value
        if (!settings.enabled) return
        
        val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
        val events = SeasonalEventCalculator.eventsForYear(currentYear) + SeasonalEventCalculator.eventsForYear(currentYear + 1)
        nativeNotificationProvider?.scheduleSeasonal(events)
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
}

actual fun createNotificationManager(): NotificationManager {
    return object : KoinComponent {
        val preferences: NotificationPreferences by inject()
        val manager = IosNotificationManagerImpl(preferences)
    }.manager
}

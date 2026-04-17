package com.kobby.hymnal.core.notifications

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface NativeNotificationProvider {
    fun scheduleWeekly()
    fun scheduleInactivity()
    fun scheduleSeasonal()
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
        nativeNotificationProvider?.scheduleSeasonal()
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

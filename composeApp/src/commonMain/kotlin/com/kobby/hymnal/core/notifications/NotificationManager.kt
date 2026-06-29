package com.kobby.hymnal.core.notifications

interface NotificationManager {
    fun scheduleWeekly(settings: NotificationSettings)
    fun scheduleInactivity(settings: NotificationSettings)
    fun scheduleSeasonal(settings: NotificationSettings)
    fun syncCampaignSubscription()
    fun cancelAll()
    fun requestPermission()
    fun sendTestNotification()
}

expect fun createNotificationManager(): NotificationManager

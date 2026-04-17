package com.kobby.hymnal.core.notifications

interface NotificationManager {
    fun scheduleWeekly()
    fun scheduleInactivity()
    fun scheduleSeasonal()
    fun syncCampaignSubscription()
    fun cancelAll()
    fun requestPermission()
}

expect fun createNotificationManager(): NotificationManager

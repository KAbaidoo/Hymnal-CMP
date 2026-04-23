package com.kobby.hymnal.di

import com.kobby.hymnal.core.notifications.NotificationPreferences
import com.kobby.hymnal.core.notifications.NotificationScheduler
import com.kobby.hymnal.core.notifications.createNotificationManager
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationPreferences(get()) }
    single { createNotificationManager() }
    single { NotificationScheduler(get(), get(), get(), get()) }
}

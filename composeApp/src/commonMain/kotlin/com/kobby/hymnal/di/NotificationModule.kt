package com.kobby.hymnal.di

import com.kobby.hymnal.core.notifications.NotificationPreferences
import com.kobby.hymnal.core.notifications.NotificationScheduler
import com.kobby.hymnal.core.notifications.createNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationPreferences(get()) }
    single { createNotificationManager() }
    single(named("notificationScope")) { 
        CoroutineScope(SupervisorJob() + Dispatchers.Default) 
    }
    single { NotificationScheduler(get(), get(), get(), get(), get(named("notificationScope"))) }
}

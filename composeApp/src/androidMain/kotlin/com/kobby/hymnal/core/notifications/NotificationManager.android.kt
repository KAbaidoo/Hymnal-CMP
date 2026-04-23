package com.kobby.hymnal.core.notifications

import android.Manifest
import android.app.NotificationChannel
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessaging
import com.kobby.hymnal.core.util.ActivityProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Year
import android.app.NotificationManager as AndroidNotificationManager

class AndroidNotificationManagerImpl(
    private val context: Context,
    private val preferences: NotificationPreferences,
    private val activityProvider: ActivityProvider
) : NotificationManager {
    private val campaignTopic = "hymnal_campaigns"

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

        val weeklyChannel = NotificationChannel(
            NotificationChannels.WEEKLY,
            "Weekly Reminders",
            AndroidNotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Sunday morning hymn reminders"
        }

        val inactivityChannel = NotificationChannel(
            NotificationChannels.INACTIVITY,
            "Inactivity Nudges",
            AndroidNotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Gentle reminders when you have been away"
        }

        val campaignChannel = NotificationChannel(
            NotificationChannels.CAMPAIGN,
            "Campaign Notifications",
            AndroidNotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Announcements and campaign messages"
        }

        val seasonalChannel = NotificationChannel(
            NotificationChannels.SEASONAL,
            "Seasonal Reminders",
            AndroidNotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Easter, Christmas and major feast reminders"
        }

        notificationManager.createNotificationChannels(
            listOf(weeklyChannel, inactivityChannel, campaignChannel, seasonalChannel)
        )
    }

    override fun scheduleWeekly(settings: NotificationSettings) {
        if (!settings.enabled || !settings.weeklyEnabled) {
            WorkManager.getInstance(context).cancelUniqueWork(NotificationWorkNames.WEEKLY_ONE_TIME)
            return
        }

        AndroidNotificationWorkScheduler.scheduleNextWeekly(
            context,
            hour = NotificationDefaults.SUNDAY_HOUR,
            minute = NotificationDefaults.SUNDAY_MINUTE
        )
    }

    override fun scheduleInactivity(settings: NotificationSettings) {
        if (!settings.enabled || !settings.inactivityEnabled) {
            WorkManager.getInstance(context).cancelUniqueWork(NotificationWorkNames.INACTIVITY_ONE_TIME)
            return
        }

        AndroidNotificationWorkScheduler.scheduleInactivity(
            context = context,
            lastActiveMillis = settings.lastActiveTimestampMs,
            inactivityDays = settings.inactivityDays
        )
    }

    override fun scheduleSeasonal(settings: NotificationSettings) {
        if (!settings.enabled || !settings.seasonalEnabled) {
            WorkManager.getInstance(context).cancelAllWorkByTag(NotificationWorkNames.TAG_SEASONAL)
            return
        }

        val currentYear = Year.now().value
        val events = SeasonalEventCalculator.eventsForYear(currentYear) +
            SeasonalEventCalculator.eventsForYear(currentYear + 1)

        AndroidNotificationWorkScheduler.scheduleSeasonal(
            context = context,
            seasonalEvents = events,
            hour = NotificationDefaults.SEASONAL_HOUR,
            minute = NotificationDefaults.SEASONAL_MINUTE
        )
    }

    override fun syncCampaignSubscription() {
        // Campaign delivery is externally controlled. Keep topic subscription active.
        FirebaseMessaging.getInstance().subscribeToTopic(campaignTopic)
    }

    override fun cancelAll() {
        val wm = WorkManager.getInstance(context)
        wm.cancelUniqueWork(NotificationWorkNames.WEEKLY_ONE_TIME)
        wm.cancelUniqueWork(NotificationWorkNames.INACTIVITY_ONE_TIME)
        wm.cancelAllWorkByTag(NotificationWorkNames.TAG_WEEKLY)
        wm.cancelAllWorkByTag(NotificationWorkNames.TAG_INACTIVITY)
        wm.cancelAllWorkByTag(NotificationWorkNames.TAG_SEASONAL)
    }

    override fun requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) return

        activityProvider.currentActivity?.let { activity ->
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                5001
            )
        }
    }
}

actual fun createNotificationManager(): NotificationManager {
    return object : KoinComponent {
        val context: Context by inject()
        val preferences: NotificationPreferences by inject()
        val activityProvider: ActivityProvider by inject()
        val manager = AndroidNotificationManagerImpl(context, preferences, activityProvider)
    }.manager
}

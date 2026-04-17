package com.kobby.hymnal.core.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WeeklyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferences = NotificationPreferences(com.russhwolf.settings.Settings())
        val settings = preferences.notificationSettings.value

        if (!settings.enabled) {
            return Result.success()
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotificationChannels.WEEKLY)
            .setContentTitle(NotificationDefaults.WEEKLY_TITLE)
            .setContentText(NotificationDefaults.WEEKLY_BODY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(101, notification)

        AndroidNotificationWorkScheduler.scheduleNextWeekly(
            context = context,
            hour = NotificationDefaults.SUNDAY_HOUR,
            minute = NotificationDefaults.SUNDAY_MINUTE
        )

        return Result.success()
    }
}

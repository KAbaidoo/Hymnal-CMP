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

        if (!settings.enabled || !settings.weeklyEnabled) {
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

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_category", NotificationCategory.WEEKLY.name.lowercase())
        }

        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            101,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannels.WEEKLY)
            .setContentTitle(settings.weeklyTitle)
            .setContentText(settings.weeklyBody)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
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

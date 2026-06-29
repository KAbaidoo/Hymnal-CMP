package com.kobby.hymnal.core.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kobby.hymnal.R

class SeasonalReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferences = NotificationPreferences(com.russhwolf.settings.Settings())
        val settings = preferences.notificationSettings.value

        if (!settings.enabled || !settings.seasonalEnabled) {
            return Result.success()
        }

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val title = inputData.getString(AndroidNotificationWorkScheduler.KEY_TITLE)
            ?: "Seasonal hymns"
        val body = inputData.getString(AndroidNotificationWorkScheduler.KEY_BODY)
            ?: "Open Hymnal for today’s service."
        val eventId = inputData.getString(AndroidNotificationWorkScheduler.KEY_EVENT_ID) ?: "301"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_category", NotificationCategory.SEASONAL.name.lowercase())
            putExtra("event_id", eventId)
        }

        val pendingIntent = intent?.let {
            android.app.PendingIntent.getActivity(
                context,
                eventId.hashCode(),
                it,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationBuilder = NotificationCompat.Builder(context, NotificationChannels.SEASONAL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)

        pendingIntent?.let {
            notificationBuilder.setContentIntent(it)
        }

        notificationManager.notify(eventId.hashCode(), notificationBuilder.build())
        return Result.success()
    }
}

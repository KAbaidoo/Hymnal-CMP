package com.kobby.hymnal.core.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class InactivityWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferences = NotificationPreferences(com.russhwolf.settings.Settings())
        val settings = preferences.notificationSettings.value

        if (!settings.enabled) {
            return Result.success()
        }

        val inactivityThresholdMs = TimeUnit.DAYS.toMillis(NotificationDefaults.INACTIVITY_DAYS.toLong())
        val now = System.currentTimeMillis()
        val inactiveDuration = now - settings.lastActiveTimestampMs

        if (settings.lastActiveTimestampMs > 0L && inactiveDuration < inactivityThresholdMs) {
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

        val notification = NotificationCompat.Builder(context, NotificationChannels.INACTIVITY)
            .setContentTitle(NotificationDefaults.NUDGE_TITLE)
            .setContentText(NotificationDefaults.NUDGE_BODY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(102, notification)
        return Result.success()
    }
}

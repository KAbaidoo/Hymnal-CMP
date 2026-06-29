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
import java.util.concurrent.TimeUnit

class InactivityWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val preferences = NotificationPreferences(com.russhwolf.settings.Settings())
        val settings = preferences.notificationSettings.value

        if (!settings.enabled || !settings.inactivityEnabled) {
            return Result.success()
        }

        val inactivityThresholdMs = TimeUnit.DAYS.toMillis(settings.inactivityDays.toLong())
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

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_category", NotificationCategory.INACTIVITY.name.lowercase())
        }

        val pendingIntent = intent?.let {
            android.app.PendingIntent.getActivity(
                context,
                102,
                it,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
        }

        val notificationBuilder = NotificationCompat.Builder(context, NotificationChannels.INACTIVITY)
            .setContentTitle(settings.inactivityTitle)
            .setContentText(settings.inactivityBody)
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)

        pendingIntent?.let {
            notificationBuilder.setContentIntent(it)
        }

        notificationManager.notify(102, notificationBuilder.build())
        return Result.success()
    }
}

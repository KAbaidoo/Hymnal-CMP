package com.kobby.hymnal.core.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SeasonalReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
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

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotificationChannels.SEASONAL)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(inputData.getString(AndroidNotificationWorkScheduler.KEY_EVENT_ID)?.hashCode() ?: 301, notification)
        return Result.success()
    }
}

package com.kobby.hymnal.core.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kobby.hymnal.MainActivity
import com.kobby.hymnal.core.trace.TraceEvents
import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.traceParams
import com.russhwolf.settings.Settings

class HymnalFirebaseMessagingService : FirebaseMessagingService() {

    private val traceManager: TraceManager by lazy { org.koin.core.context.GlobalContext.get().get() }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val settings = Settings()
        settings.putString("fcm_registration_token", token)
        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("hymnal_campaigns")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title ?: message.data["title"] ?: "Hymnal update"
        val body = message.notification?.body ?: message.data["body"] ?: "Open Hymnal for the latest update."

        traceManager.track(
            TraceEvents.CAMPAIGN_RECEIVED,
            traceParams(
                "message_id" to (message.messageId ?: "unknown"),
                "from" to (message.from ?: "unknown")
            )
        )
        traceManager.track(
            TraceEvents.NOTIFICATION_RECEIVED,
            traceParams("category" to NotificationCategory.CAMPAIGN.name.lowercase())
        )

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("notification_category", NotificationCategory.CAMPAIGN.name.lowercase())
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NotificationChannels.CAMPAIGN)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(message.messageId?.hashCode() ?: 2001, notification)
    }
}

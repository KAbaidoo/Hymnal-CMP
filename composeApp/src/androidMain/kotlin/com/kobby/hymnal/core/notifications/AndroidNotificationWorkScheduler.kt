package com.kobby.hymnal.core.notifications

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object AndroidNotificationWorkScheduler {

    const val KEY_TITLE = "key_title"
    const val KEY_BODY = "key_body"
    const val KEY_EVENT_ID = "key_event_id"

    fun scheduleNextWeekly(context: Context, hour: Int, minute: Int) {
        val now = ZonedDateTime.now()
        val dayDifference = (7 + (7 - now.dayOfWeek.value)) % 7 // Sunday = 7
        var nextSunday = now.plusDays(dayDifference.toLong())
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        if (!nextSunday.isAfter(now)) {
            nextSunday = nextSunday.plusWeeks(1)
        }

        val initialDelay = Duration.between(now, nextSunday).toMillis().coerceAtLeast(1L)

        val request = OneTimeWorkRequestBuilder<WeeklyReminderWorker>()
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(NotificationWorkNames.TAG_WEEKLY)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            NotificationWorkNames.WEEKLY_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun scheduleInactivity(context: Context, lastActiveMillis: Long, inactivityDays: Int) {
        val delay = if (lastActiveMillis <= 0L) {
            TimeUnit.DAYS.toMillis(inactivityDays.toLong())
        } else {
            val targetMillis = lastActiveMillis + TimeUnit.DAYS.toMillis(inactivityDays.toLong())
            (targetMillis - System.currentTimeMillis()).coerceAtLeast(1L)
        }

        val request = OneTimeWorkRequestBuilder<InactivityWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(NotificationWorkNames.TAG_INACTIVITY)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            NotificationWorkNames.INACTIVITY_ONE_TIME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    fun scheduleSeasonal(
        context: Context,
        seasonalEvents: List<SeasonalNotificationEvent>,
        hour: Int,
        minute: Int
    ) {
        val zone = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zone)

        seasonalEvents.forEach { event ->
            val scheduledAt = event.date.toZonedDateTimeAt(hour = hour, minute = minute, zoneId = zone)
            if (!scheduledAt.isAfter(now)) {
                return@forEach
            }

            val delay = Duration.between(now, scheduledAt).toMillis().coerceAtLeast(1L)
            val input = Data.Builder()
                .putString(KEY_TITLE, event.title)
                .putString(KEY_BODY, event.body)
                .putString(KEY_EVENT_ID, event.id)
                .build()

            val request = OneTimeWorkRequestBuilder<SeasonalReminderWorker>()
                .setInputData(input)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(NotificationWorkNames.TAG_SEASONAL)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                NotificationWorkNames.SEASONAL_PREFIX + event.id,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    private fun kotlinx.datetime.LocalDate.toZonedDateTimeAt(
        hour: Int,
        minute: Int,
        zoneId: ZoneId
    ): ZonedDateTime {
        val localDate = LocalDate.of(year, monthNumber, dayOfMonth)
        val localDateTime = LocalDateTime.of(localDate, LocalTime.of(hour, minute))
        return ZonedDateTime.of(localDateTime, zoneId)
    }
}

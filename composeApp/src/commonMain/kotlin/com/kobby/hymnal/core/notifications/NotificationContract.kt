package com.kobby.hymnal.core.notifications

object NotificationChannels {
    const val WEEKLY = "weekly_reminders"
    const val INACTIVITY = "inactivity_nudges"
    const val CAMPAIGN = "campaign_notifications"
    const val SEASONAL = "seasonal_notifications"
}

object NotificationWorkNames {
    const val WEEKLY_ONE_TIME = "notification_weekly_one_time"
    const val INACTIVITY_ONE_TIME = "notification_inactivity_one_time"
    const val SEASONAL_PREFIX = "notification_seasonal_"

    const val TAG_WEEKLY = "tag_notification_weekly"
    const val TAG_INACTIVITY = "tag_notification_inactivity"
    const val TAG_SEASONAL = "tag_notification_seasonal"
}

object NotificationDefaults {
    const val SUNDAY_HOUR = 7
    const val SUNDAY_MINUTE = 30
    const val INACTIVITY_DAYS = 3
    const val SEASONAL_HOUR = 8
    const val SEASONAL_MINUTE = 0

    const val WEEKLY_TITLE = "Your hymns are right here in your pocket"
    const val WEEKLY_BODY = "Open Hymnal and prepare for Sunday worship."

    const val NUDGE_TITLE = "Start your morning with a hymn"
    const val NUDGE_BODY = "Discover a hymn for today."
}

enum class NotificationCategory {
    WEEKLY,
    INACTIVITY,
    SEASONAL,
    CAMPAIGN
}

enum class SeasonalEventType {
    CHRISTMAS,
    EASTER,
    GOOD_FRIDAY,
    PALM_SUNDAY,
    ASCENSION,
    PENTECOST
}

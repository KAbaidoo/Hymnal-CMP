package com.kobby.hymnal.core.notifications

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/**
 * Gregorian (Western) Easter calculation using Meeus/Jones/Butcher algorithm.
 */
object SeasonalEventCalculator {

    fun easterSunday(year: Int): LocalDate {
        val a = year % 19
        val b = year / 100
        val c = year % 100
        val d = b / 4
        val e = b % 4
        val f = (b + 8) / 25
        val g = (b - f + 1) / 3
        val h = (19 * a + b - d - g + 15) % 30
        val i = c / 4
        val k = c % 4
        val l = (32 + 2 * e + 2 * i - h - k) % 7
        val m = (a + 11 * h + 22 * l) / 451
        val month = (h + l - 7 * m + 114) / 31
        val day = ((h + l - 7 * m + 114) % 31) + 1

        return LocalDate(year, month, day)
    }

    fun eventsForYear(year: Int): List<SeasonalNotificationEvent> {
        val easter = easterSunday(year)
        val palmSunday = easter.plus(DatePeriod(days = -7))
        val goodFriday = easter.plus(DatePeriod(days = -2))
        val ascension = easter.plus(DatePeriod(days = 39))
        val pentecost = easter.plus(DatePeriod(days = 49))
        val christmas = LocalDate(year, 12, 25)

        return listOf(
            SeasonalNotificationEvent(
                id = "seasonal_${year}_palm_sunday",
                type = SeasonalEventType.PALM_SUNDAY,
                date = palmSunday,
                title = "Palm Sunday is here",
                body = "Open Hymnal for Palm Sunday hymns."
            ),
            SeasonalNotificationEvent(
                id = "seasonal_${year}_good_friday",
                type = SeasonalEventType.GOOD_FRIDAY,
                date = goodFriday,
                title = "Good Friday hymns",
                body = "Prepare for Good Friday with the right hymns."
            ),
            SeasonalNotificationEvent(
                id = "seasonal_${year}_easter",
                type = SeasonalEventType.EASTER,
                date = easter,
                title = "He is risen",
                body = "Find Easter hymns for today’s service."
            ),
            SeasonalNotificationEvent(
                id = "seasonal_${year}_ascension",
                type = SeasonalEventType.ASCENSION,
                date = ascension,
                title = "Ascension Day hymns",
                body = "Open Hymnal and prepare for Ascension worship."
            ),
            SeasonalNotificationEvent(
                id = "seasonal_${year}_pentecost",
                type = SeasonalEventType.PENTECOST,
                date = pentecost,
                title = "Pentecost hymns",
                body = "Prepare your Pentecost hymn set."
            ),
            SeasonalNotificationEvent(
                id = "seasonal_${year}_christmas",
                type = SeasonalEventType.CHRISTMAS,
                date = christmas,
                title = "Christmas hymns ready",
                body = "Celebrate with your Christmas hymn selections."
            )
        )
    }
}

data class SeasonalNotificationEvent(
    val id: String,
    val type: SeasonalEventType,
    val date: LocalDate,
    val title: String,
    val body: String
)

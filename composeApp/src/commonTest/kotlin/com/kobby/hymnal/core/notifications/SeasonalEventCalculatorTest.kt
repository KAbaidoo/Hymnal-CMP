package com.kobby.hymnal.core.notifications

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.datetime.LocalDate

class SeasonalEventCalculatorTest {

    @Test
    fun `easter Sunday matches known 2026 date`() {
        val easter = SeasonalEventCalculator.easterSunday(2026)
        assertEquals(LocalDate(2026, 4, 5), easter)
    }

    @Test
    fun `easter Sunday matches known 2027 date`() {
        val easter = SeasonalEventCalculator.easterSunday(2027)
        assertEquals(LocalDate(2027, 3, 28), easter)
    }

    @Test
    fun `seasonal events include christmas and easter derived days`() {
        val events = SeasonalEventCalculator.eventsForYear(2026)
        assertEquals(6, events.size)

        val ids = events.map { it.id }.toSet()
        assertTrue(ids.contains("seasonal_2026_christmas"))
        assertTrue(ids.contains("seasonal_2026_easter"))
        assertTrue(ids.contains("seasonal_2026_good_friday"))
        assertTrue(ids.contains("seasonal_2026_pentecost"))

        val easter = events.first { it.type == SeasonalEventType.EASTER }
        val goodFriday = events.first { it.type == SeasonalEventType.GOOD_FRIDAY }
        assertEquals(LocalDate(2026, 4, 5), easter.date)
        assertEquals(LocalDate(2026, 4, 3), goodFriday.date)
    }
}

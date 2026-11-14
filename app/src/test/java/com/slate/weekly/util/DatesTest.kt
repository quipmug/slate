package com.slate.weekly.util

import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DatesTest {

    @Test
    fun `week starts on monday`() {
        assertEquals(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 11).startOfWeek())
        assertEquals(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 15).startOfWeek())
        assertEquals(LocalDate.of(2026, 11, 9), LocalDate.of(2026, 11, 9).startOfWeek())
    }

    @Test
    fun `week range stays readable across months`() {
        assertEquals("9–15 nov", weekRangeLabel(LocalDate.of(2026, 11, 9)))
        assertEquals("30 nov–6 dec", weekRangeLabel(LocalDate.of(2026, 11, 30)))
        assertEquals("28 dec–3 jan", weekRangeLabel(LocalDate.of(2026, 12, 28)))
    }

    @Test
    fun `week days are monday to sunday`() {
        assertEquals(
            listOf(
                LocalDate.of(2026, 11, 9),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 11),
                LocalDate.of(2026, 11, 12),
                LocalDate.of(2026, 11, 13),
                LocalDate.of(2026, 11, 14),
                LocalDate.of(2026, 11, 15),
            ),
            weekDays(LocalDate.of(2026, 11, 9)),
        )
    }

    @Test
    fun `trail weeks end on the given monday`() {
        val weeks = weeksEndingAt(LocalDate.of(2026, 11, 9), 3)
        assertEquals(
            listOf(
                LocalDate.of(2026, 10, 26),
                LocalDate.of(2026, 11, 2),
                LocalDate.of(2026, 11, 9),
            ),
            weeks,
        )
        assertEquals(emptyList<LocalDate>(), weeksEndingAt(LocalDate.of(2026, 11, 9), 0))
    }

    @Test
    fun `coerce week keeps navigation inside the habit lifetime`() {
        val current = LocalDate.of(2026, 11, 9)
        val first = LocalDate.of(2026, 10, 26)
        assertEquals(LocalDate.of(2026, 11, 2), coerceWeek(LocalDate.of(2026, 11, 2), first, current))
        assertEquals(first, coerceWeek(LocalDate.of(2026, 10, 12), first, current))
        assertEquals(current, coerceWeek(LocalDate.of(2026, 12, 7), first, current))
    }

    @Test
    fun `coerce week survives a creation date in the future`() {
        val current = LocalDate.of(2026, 11, 9)
        val future = LocalDate.of(2026, 12, 7)
        assertEquals(current, coerceWeek(current, future, current))
        assertEquals(current, coerceWeek(future, future, current))
    }

    @Test
    fun `time is padded for the reminder row`() {
        assertEquals("07:05", formatTime(7, 5))
        assertEquals("20:00", formatTime(20, 0))
    }
}

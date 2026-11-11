package com.slate.weekly.util

import com.slate.weekly.data.Habit
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class DatesTest {

    private fun habit(
        target: Int,
        checks: Set<LocalDate>,
        created: LocalDate = LocalDate.of(2026, 1, 1),
    ) = Habit(
        id = "habit",
        name = "a run",
        target = target,
        createdDay = created.toEpochDay(),
        checks = checks.map { it.toEpochDay() }.toSet(),
    )

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
    fun `week checks only count days in that week`() {
        val habit = habit(
            target = 3,
            checks = setOf(
                LocalDate.of(2026, 11, 29),
                LocalDate.of(2026, 11, 30),
                LocalDate.of(2026, 12, 6),
                LocalDate.of(2026, 12, 7),
            ),
        )
        assertEquals(2, weekChecks(habit, LocalDate.of(2026, 11, 30)))
        assertEquals(1, weekChecks(habit, LocalDate.of(2026, 12, 7)))
    }

    @Test
    fun `streak counts this week when the target is met`() {
        val habit = habit(
            target = 3,
            checks = setOf(
                LocalDate.of(2026, 11, 3),
                LocalDate.of(2026, 11, 4),
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 9),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 11),
            ),
        )
        assertEquals(2, weeksInARow(habit, LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun `streak skips an unfinished this week`() {
        val habit = habit(
            target = 3,
            checks = setOf(
                LocalDate.of(2026, 11, 3),
                LocalDate.of(2026, 11, 4),
                LocalDate.of(2026, 11, 5),
                LocalDate.of(2026, 11, 9),
            ),
        )
        assertEquals(1, weeksInARow(habit, LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun `streak never reaches before the habit was created`() {
        val habit = habit(
            target = 3,
            checks = setOf(
                LocalDate.of(2026, 11, 3),
                LocalDate.of(2026, 11, 4),
                LocalDate.of(2026, 11, 5),
            ),
            created = LocalDate.of(2026, 11, 9),
        )
        assertEquals(0, weeksInARow(habit, LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun `days kept are counted once across habits`() {
        val first = habit(
            target = 3,
            checks = setOf(
                LocalDate.of(2026, 1, 2),
                LocalDate.of(2026, 11, 11),
            ),
        )
        val second = habit(
            target = 2,
            checks = setOf(
                LocalDate.of(2026, 1, 2),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2025, 12, 31),
            ),
        )
        assertEquals(3, daysKeptThisYear(listOf(first, second), LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun `time is padded for the reminder row`() {
        assertEquals("07:05", formatTime(7, 5))
        assertEquals("20:00", formatTime(20, 0))
    }
}

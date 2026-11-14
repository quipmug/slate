package com.slate.weekly.domain

import com.slate.weekly.data.Habit
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HabitProgressTest {

    private fun habit(
        target: Int = 3,
        checks: Set<LocalDate> = emptySet(),
        created: LocalDate = LocalDate.of(2026, 1, 1),
    ) = Habit(
        id = "habit",
        name = "a run",
        target = target,
        createdDay = created.toEpochDay(),
        checks = checks.map { it.toEpochDay() }.toSet(),
    )

    @Test
    fun `week checks only count days in that week`() {
        val habit = habit(
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
    fun `done in week is capped by the target`() {
        val habit = habit(
            target = 2,
            checks = setOf(
                LocalDate.of(2026, 11, 9),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 11),
            ),
        )
        assertEquals(2, doneInWeek(habit, LocalDate.of(2026, 11, 9)))
        assertTrue(isWeekComplete(habit, LocalDate.of(2026, 11, 9)))
    }

    @Test
    fun `streak counts this week when the target is met`() {
        val habit = habit(
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
    fun `open habits exclude checked days and finished weeks`() {
        val today = LocalDate.of(2026, 11, 11)
        val open = habit(target = 3)
        val checkedToday = habit(
            target = 3,
            checks = setOf(today),
        )
        val weekDone = habit(
            target = 2,
            checks = setOf(
                LocalDate.of(2026, 11, 9),
                LocalDate.of(2026, 11, 10),
            ),
        )
        val future = habit(
            target = 3,
            created = LocalDate.of(2026, 11, 12),
        )
        assertEquals(
            listOf(open),
            openHabits(listOf(open, checkedToday, weekDone, future), today),
        )
    }

    @Test
    fun `days kept are counted once across habits`() {
        val first = habit(
            checks = setOf(
                LocalDate.of(2026, 1, 2),
                LocalDate.of(2026, 11, 11),
            ),
        )
        val second = habit(
            checks = setOf(
                LocalDate.of(2026, 1, 2),
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2025, 12, 31),
            ),
        )
        assertEquals(3, daysKeptThisYear(listOf(first, second), LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun `days kept in year stop at today`() {
        val habit = habit(
            checks = setOf(
                LocalDate.of(2026, 11, 10),
                LocalDate.of(2026, 11, 11),
                LocalDate.of(2026, 11, 12),
                LocalDate.of(2025, 12, 31),
            ),
        )
        assertEquals(2, daysKeptInYear(habit, 2026, LocalDate.of(2026, 11, 11)))
    }

    @Test
    fun `year heatmap includes whole weeks around the year`() {
        val weeks = weeksOfYear(2026)
        assertEquals(53, weeks.size)
        assertTrue(weeks.first() <= LocalDate.of(2026, 1, 1))
        assertFalse(weeks.last().plusDays(6) < LocalDate.of(2026, 12, 31))
    }
}

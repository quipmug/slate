package com.slate.weekly.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HabitRulesTest {

    private fun habit(
        id: String = "habit",
        name: String = "a run",
        target: Int = 3,
        createdDay: Long = 100,
        checks: Set<Long> = emptySet(),
    ) = Habit(id, name, target, createdDay, checks)

    @Test
    fun `target and name are cleaned`() {
        val habits = HabitRules.normalize(
            listOf(habit(name = "  a run  ", target = 99)),
        )
        assertEquals("a run", habits.single().name)
        assertEquals(HabitRules.MAX_TARGET, habits.single().target)
    }

    @Test
    fun `blank and duplicate habits are dropped`() {
        val habits = HabitRules.normalize(
            listOf(
                habit(id = "one", name = "a run"),
                habit(id = "one", name = "another"),
                habit(id = "two", name = "   "),
            ),
        )
        assertEquals(listOf("one"), habits.map { it.id })
    }

    @Test
    fun `checks before creation are dropped`() {
        val habits = HabitRules.normalize(
            listOf(habit(createdDay = 100, checks = setOf(99, 100, 101))),
        )
        assertEquals(setOf(100L, 101L), habits.single().checks)
    }

    @Test
    fun `long names stay within the limit`() {
        val habits = HabitRules.normalize(listOf(habit(name = "x".repeat(80))))
        assertTrue(habits.single().name.length == HabitRules.MAX_NAME_LENGTH)
    }

    @Test
    fun `toggle ignores days outside the habit lifetime`() {
        val habit = habit(createdDay = 100)
        assertEquals(habit, HabitRules.toggled(habit, day = 99, today = 120))
        assertEquals(habit, HabitRules.toggled(habit, day = 121, today = 120))
    }

    @Test
    fun `toggle adds and removes a check`() {
        val habit = habit(createdDay = 100)
        val checked = HabitRules.toggled(habit, day = 105, today = 120)
        assertEquals(setOf(105L), checked.checks)
        assertEquals(emptySet<Long>(), HabitRules.toggled(checked, day = 105, today = 120).checks)
    }
}

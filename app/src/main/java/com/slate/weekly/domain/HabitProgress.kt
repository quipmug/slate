package com.slate.weekly.domain

import com.slate.weekly.data.Habit
import com.slate.weekly.util.startOfWeek
import java.time.LocalDate
import kotlin.math.min

fun weekChecks(habit: Habit, monday: LocalDate): Int {
    val start = monday.toEpochDay()
    val end = start + 6
    return habit.checks.count { it in start..end }
}

fun doneInWeek(habit: Habit, monday: LocalDate): Int =
    min(weekChecks(habit, monday), habit.target)

fun isWeekComplete(habit: Habit, monday: LocalDate): Boolean =
    weekChecks(habit, monday) >= habit.target

fun weeksInARow(habit: Habit, today: LocalDate): Int {
    if (habit.target <= 0) return 0
    val firstMonday = LocalDate.ofEpochDay(habit.createdDay).startOfWeek()
    var monday = today.startOfWeek()
    if (!isWeekComplete(habit, monday)) monday = monday.minusWeeks(1)
    var streak = 0
    while (monday >= firstMonday && isWeekComplete(habit, monday)) {
        streak++
        monday = monday.minusWeeks(1)
    }
    return streak
}

fun checkedDaysThisYear(habits: List<Habit>, today: LocalDate): Set<Long> {
    val start = LocalDate.of(today.year, 1, 1).toEpochDay()
    val end = today.toEpochDay()
    return habits
        .flatMap { it.checks }
        .filterTo(HashSet()) { it in start..end }
}

fun daysKeptThisYear(habits: List<Habit>, today: LocalDate): Int =
    checkedDaysThisYear(habits, today).size

fun daysKeptInYear(habit: Habit, year: Int, today: LocalDate): Int {
    val start = LocalDate.of(year, 1, 1).toEpochDay()
    val end = min(LocalDate.of(year, 12, 31).toEpochDay(), today.toEpochDay())
    return habit.checks.count { it in start..end }
}

fun weeksOfYear(year: Int): List<LocalDate> {
    val start = LocalDate.of(year, 1, 1).startOfWeek()
    val end = LocalDate.of(year, 12, 31)
    val count = ((end.toEpochDay() - start.toEpochDay()) / 7).toInt() + 1
    return List(count) { start.plusWeeks(it.toLong()) }
}

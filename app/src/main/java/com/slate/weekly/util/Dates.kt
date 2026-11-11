package com.slate.weekly.util

import com.slate.weekly.data.Habit
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

fun LocalDate.startOfWeek(): LocalDate = minusDays((dayOfWeek.value - 1).toLong())

fun weekDays(monday: LocalDate): List<LocalDate> = (0L..6L).map { monday.plusDays(it) }

fun dayLabel(date: LocalDate, locale: Locale = Locale.getDefault()): String =
    date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).lowercase(locale).take(2)

fun monthName(month: Month, short: Boolean, locale: Locale = Locale.getDefault()): String =
    month.getDisplayName(if (short) TextStyle.SHORT else TextStyle.FULL, locale).lowercase(locale)

fun monthShortLabel(month: Month, locale: Locale = Locale.getDefault()): String =
    month.getDisplayName(TextStyle.SHORT, locale).lowercase(locale).removeSuffix(".")

fun weekRangeLabel(monday: LocalDate, locale: Locale = Locale.getDefault()): String {
    val sunday = monday.plusDays(6)
    return if (monday.month == sunday.month) {
        "${monday.dayOfMonth} – ${sunday.dayOfMonth} ${monthName(monday.month, false, locale)}"
    } else {
        "${monday.dayOfMonth} ${monthName(monday.month, true, locale)} – " +
            "${sunday.dayOfMonth} ${monthName(sunday.month, true, locale)}"
    }
}

fun weekChecks(habit: Habit, monday: LocalDate): Int {
    val start = monday.toEpochDay()
    val end = start + 6
    return habit.checks.count { it in start..end }
}

fun weeksInARow(habit: Habit, today: LocalDate): Int {
    if (habit.target <= 0) return 0
    var monday = today.startOfWeek()
    if (weekChecks(habit, monday) < habit.target) monday = monday.minusWeeks(1)
    var streak = 0
    while (weekChecks(habit, monday) >= habit.target && streak < 260) {
        streak++
        monday = monday.minusWeeks(1)
    }
    return streak
}

fun daysKeptThisYear(habits: List<Habit>, today: LocalDate): Int {
    val start = LocalDate.of(today.year, 1, 1)
    val elapsed = ChronoUnit.DAYS.between(start, today).toInt() + 1
    val checked = habits.flatMap { it.checks }.toSet()
    return (0 until elapsed).count { start.plusDays(it.toLong()).toEpochDay() in checked }
}

fun formatTime(hour: Int, minute: Int): String =
    String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

package com.slate.weekly.util

import com.slate.weekly.data.Habit
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val SlateLocale = Locale.ENGLISH

fun LocalDate.startOfWeek(): LocalDate = minusDays((dayOfWeek.value - 1).toLong())

fun weekDays(monday: LocalDate): List<LocalDate> = (0L..6L).map { monday.plusDays(it) }

fun dayLabel(date: LocalDate): String =
    date.dayOfWeek.getDisplayName(TextStyle.SHORT, SlateLocale).lowercase(SlateLocale).take(2)

fun dayDescription(date: LocalDate): String =
    date.dayOfWeek.getDisplayName(TextStyle.FULL, SlateLocale).lowercase(SlateLocale) +
        " ${date.dayOfMonth} ${monthName(date.month, false)}"

fun monthName(month: Month, short: Boolean): String =
    month.getDisplayName(if (short) TextStyle.SHORT else TextStyle.FULL, SlateLocale)
        .lowercase(SlateLocale)

fun weekRangeLabel(monday: LocalDate): String {
    val sunday = monday.plusDays(6)
    return if (monday.month == sunday.month) {
        "${monday.dayOfMonth}–${sunday.dayOfMonth} ${monthName(monday.month, true)}"
    } else {
        "${monday.dayOfMonth} ${monthName(monday.month, true)}–" +
            "${sunday.dayOfMonth} ${monthName(sunday.month, true)}"
    }
}

fun weekChecks(habit: Habit, monday: LocalDate): Int {
    val start = monday.toEpochDay()
    val end = start + 6
    return habit.checks.count { it in start..end }
}

fun weeksInARow(habit: Habit, today: LocalDate): Int {
    if (habit.target <= 0) return 0
    val firstMonday = LocalDate.ofEpochDay(habit.createdDay).startOfWeek()
    var monday = today.startOfWeek()
    if (weekChecks(habit, monday) < habit.target) monday = monday.minusWeeks(1)
    var streak = 0
    while (monday >= firstMonday && weekChecks(habit, monday) >= habit.target && streak < 260) {
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
    String.format(Locale.US, "%02d:%02d", hour, minute)

package com.slate.weekly.util

import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

private val SlateLocale = Locale.ENGLISH

fun LocalDate.startOfWeek(): LocalDate = minusDays((dayOfWeek.value - 1).toLong())

fun weekDays(monday: LocalDate): List<LocalDate> = (0L..6L).map { monday.plusDays(it) }

fun weeksEndingAt(monday: LocalDate, count: Int): List<LocalDate> {
    if (count <= 0) return emptyList()
    return List(count) { index -> monday.minusWeeks((count - 1 - index).toLong()) }
}

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

fun formatTime(hour: Int, minute: Int): String =
    String.format(Locale.US, "%02d:%02d", hour, minute)

package com.slate.weekly.domain

const val MAX_FOCUS_HOURS = 24

private const val HOUR_MILLIS = 3_600_000L

fun focusEnd(nowMillis: Long, hours: Int): Long =
    nowMillis + hours.coerceIn(1, MAX_FOCUS_HOURS) * HOUR_MILLIS

fun isFocusActive(untilMillis: Long, nowMillis: Long): Boolean =
    untilMillis > nowMillis

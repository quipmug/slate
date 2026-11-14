package com.slate.weekly.ui

import androidx.compose.runtime.saveable.Saver
import java.time.LocalDate

object SlateRoutes {
    const val Home = "home"
    const val Detail = "detail/{habitId}"
    const val Year = "year"
    const val Settings = "settings"
    const val FocusApps = "focus_apps"
}

val LocalDateSaver = Saver<LocalDate, Long>(
    save = { it.toEpochDay() },
    restore = { LocalDate.ofEpochDay(it) },
)

package com.slate.weekly.data

import androidx.compose.runtime.Immutable

enum class ThemeMode(val key: String) {
    Light("light"),
    Dark("dark"),
    Auto("auto"),
    ;

    companion object {
        fun fromKey(key: String?): ThemeMode =
            entries.firstOrNull { it.key == key } ?: Auto
    }
}

@Immutable
data class SlateSettings(
    val theme: ThemeMode = ThemeMode.Auto,
    val reminderOn: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
)

data class SlateData(
    val habits: List<Habit> = emptyList(),
    val settings: SlateSettings = SlateSettings(),
    val welcomeSeen: Boolean = false,
)

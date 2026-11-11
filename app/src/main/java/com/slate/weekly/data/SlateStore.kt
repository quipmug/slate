package com.slate.weekly.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.slateData by preferencesDataStore(name = "slate")

class SlateStore(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private val habitsKey = stringPreferencesKey("habits")
    private val themeKey = stringPreferencesKey("theme")
    private val reminderOnKey = booleanPreferencesKey("reminder_on")
    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val reminderMinuteKey = intPreferencesKey("reminder_minute")

    val habits: Flow<List<Habit>> = context.slateData.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> decode(prefs[habitsKey]) }

    val settings: Flow<SlateSettings> = context.slateData.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            SlateSettings(
                theme = prefs[themeKey] ?: "auto",
                reminderOn = prefs[reminderOnKey] ?: false,
                reminderHour = prefs[reminderHourKey] ?: 20,
                reminderMinute = prefs[reminderMinuteKey] ?: 0,
            )
        }

    suspend fun add(name: String, target: Int) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        val habit = Habit(
            id = UUID.randomUUID().toString(),
            name = clean,
            target = target.coerceIn(1, 7),
            createdDay = LocalDate.now().toEpochDay(),
        )
        mutate { it + habit }
    }

    suspend fun toggle(habitId: String, day: Long) {
        val today = LocalDate.now().toEpochDay()
        mutate { list ->
            list.map { habit ->
                if (habit.id != habitId || day < habit.createdDay || day > today) {
                    habit
                } else {
                    habit.copy(
                        checks = if (day in habit.checks) habit.checks - day else habit.checks + day,
                    )
                }
            }
        }
    }

    suspend fun rename(habitId: String, name: String) {
        val clean = name.trim()
        if (clean.isEmpty()) return
        mutate { list ->
            list.map { if (it.id == habitId) it.copy(name = clean) else it }
        }
    }

    suspend fun setTarget(habitId: String, target: Int) {
        mutate { list ->
            list.map { if (it.id == habitId) it.copy(target = target.coerceIn(1, 7)) else it }
        }
    }

    suspend fun delete(habitId: String) {
        mutate { list -> list.filterNot { it.id == habitId } }
    }

    suspend fun setTheme(theme: String) {
        context.slateData.edit { prefs -> prefs[themeKey] = theme }
    }

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.slateData.edit { prefs ->
            prefs[reminderOnKey] = enabled
            prefs[reminderHourKey] = hour.coerceIn(0, 23)
            prefs[reminderMinuteKey] = minute.coerceIn(0, 59)
        }
    }

    private suspend fun mutate(block: (List<Habit>) -> List<Habit>) {
        context.slateData.edit { prefs ->
            prefs[habitsKey] = json.encodeToString(block(decode(prefs[habitsKey])))
        }
    }

    private fun decode(raw: String?): List<Habit> {
        if (raw.isNullOrBlank()) return emptyList()
        return runCatching { json.decodeFromString<List<Habit>>(raw) }
            .getOrDefault(emptyList())
    }
}

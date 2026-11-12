package com.slate.weekly.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.slateData by preferencesDataStore(name = "slate")

class SlateStore(context: Context) {

    private val appContext = context.applicationContext
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val habitsKey = stringPreferencesKey("habits")
    private val themeKey = stringPreferencesKey("theme")
    private val reminderOnKey = booleanPreferencesKey("reminder_on")
    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val reminderMinuteKey = intPreferencesKey("reminder_minute")

    val data: Flow<SlateData> = appContext.slateData.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { prefs ->
            SlateData(
                habits = decodeHabits(prefs[habitsKey]),
                settings = SlateSettings(
                    theme = ThemeMode.fromKey(prefs[themeKey]),
                    reminderOn = prefs[reminderOnKey] ?: false,
                    reminderHour = (prefs[reminderHourKey] ?: 20).coerceIn(0, 23),
                    reminderMinute = (prefs[reminderMinuteKey] ?: 0).coerceIn(0, 59),
                ),
            )
        }

    suspend fun add(name: String, target: Int): Boolean {
        val clean = HabitRules.cleanName(name)
        if (clean.isEmpty()) return false
        val habit = Habit(
            id = UUID.randomUUID().toString(),
            name = clean,
            target = HabitRules.cleanTarget(target),
            createdDay = LocalDate.now().toEpochDay(),
        )
        mutateHabits { it + habit }
        return true
    }

    suspend fun toggle(habitId: String, day: Long) {
        val today = LocalDate.now().toEpochDay()
        mutateHabit(habitId) { habit -> HabitRules.toggled(habit, day, today) }
    }

    suspend fun rename(habitId: String, name: String) {
        val clean = HabitRules.cleanName(name)
        if (clean.isEmpty()) return
        mutateHabit(habitId) { it.copy(name = clean) }
    }

    suspend fun setTarget(habitId: String, target: Int) {
        mutateHabit(habitId) { it.copy(target = HabitRules.cleanTarget(target)) }
    }

    suspend fun delete(habitId: String) {
        mutateHabits { habits -> habits.filterNot { it.id == habitId } }
    }

    suspend fun resetHabits() {
        appContext.slateData.edit { prefs -> prefs.remove(habitsKey) }
    }

    suspend fun setTheme(theme: ThemeMode) {
        appContext.slateData.edit { prefs -> prefs[themeKey] = theme.key }
    }

    suspend fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        appContext.slateData.edit { prefs ->
            prefs[reminderOnKey] = enabled
            prefs[reminderHourKey] = hour.coerceIn(0, 23)
            prefs[reminderMinuteKey] = minute.coerceIn(0, 59)
        }
    }

    private suspend fun mutateHabit(habitId: String, transform: (Habit) -> Habit) {
        mutateHabits { habits ->
            habits.map { habit -> if (habit.id == habitId) transform(habit) else habit }
        }
    }

    private suspend fun mutateHabits(transform: (List<Habit>) -> List<Habit>) {
        appContext.slateData.edit { prefs ->
            val next = transform(decodeHabits(prefs[habitsKey]))
            prefs[habitsKey] = json.encodeToString(HabitRules.normalize(next))
        }
    }

    private fun decodeHabits(raw: String?): List<Habit> {
        if (raw.isNullOrBlank()) return emptyList()
        val parsed = runCatching { json.decodeFromString<List<Habit>>(raw) }
            .getOrDefault(emptyList())
        return HabitRules.normalize(parsed)
    }
}

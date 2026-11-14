package com.slate.weekly.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.slate.weekly.domain.focusEnd
import com.slate.weekly.domain.isFocusActive
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

@Singleton
class SlateStore @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val habitDao: HabitDao,
) {

    private val json = kotlinx.serialization.json.Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val currentSchemaVersion = 1

    private val habitsKey = stringPreferencesKey("habits")
    private val themeKey = stringPreferencesKey("theme")
    private val reminderOnKey = booleanPreferencesKey("reminder_on")
    private val reminderHourKey = intPreferencesKey("reminder_hour")
    private val reminderMinuteKey = intPreferencesKey("reminder_minute")
    private val welcomeSeenKey = booleanPreferencesKey("welcome_seen")
    private val focusUntilKey = longPreferencesKey("focus_until")
    private val blockedPackagesKey = stringSetPreferencesKey("blocked_packages")
    private val schemaVersionKey = intPreferencesKey("schema_version")

    private val Context.slateData by preferencesDataStore(name = "slate")

    private val preferences: Flow<Preferences> = appContext.slateData.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }

    val settings: Flow<SlateSettings> = preferences
        .map(::readSettings)
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)

    private val welcomeSeen: Flow<Boolean> = preferences
        .map { prefs -> prefs[welcomeSeenKey] ?: false }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)

    private val schemaVersion: Flow<Int> = preferences
        .map { prefs -> prefs[schemaVersionKey] ?: 0 }
        .distinctUntilChanged()
        .flowOn(Dispatchers.Default)

    val data: Flow<SlateData> = flow {
        migrateFromDataStoreIfNeeded()
        combine(
            habitDao.getAllHabits(),
            habitDao.getAllChecks(),
            settings,
            welcomeSeen,
            schemaVersion,
        ) { habitEntities, checkEntities, settings, seen, version ->
            val validHabits = if (version != currentSchemaVersion) emptyList() else habitEntities
            val checksByHabit = checkEntities.groupBy { it.habitId }
            val habits = validHabits.map { entity ->
                Habit(
                    id = entity.id,
                    name = entity.name,
                    target = entity.target,
                    createdDay = entity.createdDay,
                    checks = checksByHabit[entity.id].orEmpty().map { it.day }.toSet(),
                )
            }
            SlateData(habits = habits, settings = settings, welcomeSeen = seen)
        }.collect { emit(it) }
    }.flowOn(Dispatchers.Default)

    private suspend fun migrateFromDataStoreIfNeeded() {
        val prefs = appContext.slateData.data.first()
        val currentVersion = prefs[schemaVersionKey] ?: 0

        if (currentVersion >= currentSchemaVersion) return

        val rawHabits = prefs[habitsKey]

        if (rawHabits.isNullOrBlank()) {
            appContext.slateData.edit { prefs ->
                prefs[schemaVersionKey] = currentSchemaVersion
            }
            return
        }

        val parsed = runCatching {
            json.decodeFromString<List<LegacyHabit>>(rawHabits)
        }.getOrDefault(emptyList())

        val entities = parsed.map { legacy ->
            HabitEntity(
                id = legacy.id,
                name = legacy.name,
                target = legacy.target,
                createdDay = legacy.createdDay,
            )
        }
        val checks = parsed.flatMap { legacy ->
            legacy.checks.map { day -> HabitCheck(legacy.id, day) }
        }

        habitDao.insertHabits(*entities.toTypedArray())
        if (checks.isNotEmpty()) {
            habitDao.insertChecks(*checks.toTypedArray())
        }

        appContext.slateData.edit { prefs ->
            prefs[schemaVersionKey] = currentSchemaVersion
        }
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
        habitDao.upsertHabitWithChecks(habit.toEntity(), emptySet())
        return true
    }

    suspend fun toggle(habitId: String, day: Long) {
        habitDao.toggleCheck(habitId, day)
    }

    suspend fun rename(habitId: String, name: String) {
        val clean = HabitRules.cleanName(name)
        if (clean.isEmpty()) return
        val entity = habitDao.getHabitById(habitId) ?: return
        habitDao.updateHabit(entity.copy(name = clean))
    }

    suspend fun setTarget(habitId: String, target: Int) {
        val entity = habitDao.getHabitById(habitId) ?: return
        habitDao.updateHabit(entity.copy(target = HabitRules.cleanTarget(target)))
    }

    suspend fun delete(habitId: String) {
        val entity = habitDao.getHabitById(habitId) ?: return
        habitDao.deleteHabit(entity)
    }

    suspend fun resetHabits() {
        habitDao.deleteAllHabits()
    }

    suspend fun markWelcomeSeen() {
        appContext.slateData.edit { prefs -> prefs[welcomeSeenKey] = true }
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

    suspend fun setBlockedApp(packageName: String, blocked: Boolean) {
        val clean = packageName.trim()
        if (clean.isEmpty() || clean in protectedPackages(appContext)) return
        appContext.slateData.edit { prefs ->
            val settings = readSettings(prefs)
            if (isFocusActive(settings.focusUntil, System.currentTimeMillis())) return@edit
            val next = if (blocked) {
                settings.blockedPackages + clean
            } else {
                settings.blockedPackages - clean
            }
            prefs[blockedPackagesKey] = next
        }
    }

    suspend fun startFocus(hours: Int) {
        appContext.slateData.edit { prefs ->
            val settings = readSettings(prefs)
            val now = System.currentTimeMillis()
            if (isFocusActive(settings.focusUntil, now)) return@edit
            val protected = protectedPackages(appContext)
            val packages = settings.blockedPackages
                .map { it.trim() }
                .filterTo(LinkedHashSet()) { it.isNotEmpty() && it !in protected }
            if (packages.isEmpty()) return@edit
            prefs[blockedPackagesKey] = packages
            prefs[focusUntilKey] = focusEnd(now, hours)
        }
    }

    private fun readSettings(prefs: Preferences): SlateSettings = SlateSettings(
        theme = ThemeMode.fromKey(prefs[themeKey]),
        reminderOn = prefs[reminderOnKey] ?: false,
        reminderHour = (prefs[reminderHourKey] ?: 20).coerceIn(0, 23),
        reminderMinute = (prefs[reminderMinuteKey] ?: 0).coerceIn(0, 59),
        focusUntil = prefs[focusUntilKey] ?: 0L,
        blockedPackages = prefs[blockedPackagesKey] ?: emptySet(),
    )
}

// Legacy habit class for DataStore JSON migration
@Serializable
private data class LegacyHabit(
    val id: String,
    val name: String,
    val target: Int,
    val createdDay: Long,
    val checks: Set<Long> = emptySet(),
)

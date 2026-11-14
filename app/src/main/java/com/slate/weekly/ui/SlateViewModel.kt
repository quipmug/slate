package com.slate.weekly.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slate.weekly.data.Habit
import com.slate.weekly.data.SlateSettings
import com.slate.weekly.data.SlateStore
import com.slate.weekly.data.ThemeMode
import com.slate.weekly.reminder.ReminderNotifier
import com.slate.weekly.reminder.Reminders
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SlateUiState(
    val habits: List<Habit> = emptyList(),
    val settings: SlateSettings = SlateSettings(),
    val today: LocalDate = LocalDate.now(),
    val loaded: Boolean = false,
    val welcomeSeen: Boolean = false,
)

@HiltViewModel
class SlateViewModel @Inject constructor(
    private val application: Application,
    private val store: SlateStore,
) : ViewModel() {

    private val appContext = application.applicationContext
    private val today = MutableStateFlow(LocalDate.now())

    val uiState: StateFlow<SlateUiState> = combine(store.data, today) { data, date ->
        SlateUiState(
            habits = data.habits,
            settings = data.settings,
            today = date,
            loaded = true,
            welcomeSeen = data.welcomeSeen,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SlateUiState(today = today.value),
    )

    init {
        viewModelScope.launch {
            store.settings
                .map { Triple(it.reminderOn, it.reminderHour, it.reminderMinute) }
                .distinctUntilChanged()
                .collect { (enabled, hour, minute) ->
                    if (enabled) {
                        Reminders.schedule(
                            context = appContext,
                            hour = hour,
                            minute = minute,
                        )
                    } else {
                        Reminders.cancel(appContext)
                    }
                }
        }
    }

    fun refreshToday() {
        today.value = LocalDate.now()
    }

    fun add(name: String, target: Int) {
        viewModelScope.launch { store.add(name, target) }
    }

    fun toggle(habitId: String, day: LocalDate) {
        viewModelScope.launch { store.toggle(habitId, day.toEpochDay()) }
    }

    fun rename(habitId: String, name: String) {
        viewModelScope.launch { store.rename(habitId, name) }
    }

    fun setTarget(habitId: String, target: Int) {
        viewModelScope.launch { store.setTarget(habitId, target) }
    }

    fun delete(habitId: String) {
        viewModelScope.launch { store.delete(habitId) }
    }

    fun resetHabits() {
        viewModelScope.launch { store.resetHabits() }
    }

    fun markWelcomeSeen() {
        viewModelScope.launch { store.markWelcomeSeen() }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch { store.setTheme(theme) }
    }

    fun setReminder(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch { store.setReminder(enabled, hour, minute) }
    }

    fun setBlockedApp(packageName: String, blocked: Boolean) {
        viewModelScope.launch { store.setBlockedApp(packageName, blocked) }
    }

    fun startFocus(hours: Int) {
        viewModelScope.launch { store.startFocus(hours) }
    }

    fun previewReminder() {
        ReminderNotifier.preview(appContext)
    }
}

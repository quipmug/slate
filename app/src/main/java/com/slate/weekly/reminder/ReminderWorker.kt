package com.slate.weekly.reminder

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.slate.weekly.data.SlateStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val store: SlateStore,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val data = runCatching { store.data.first() }
            .getOrNull()
            ?: return Result.retry()
        val settings = data.settings
        if (!settings.reminderOn) return Result.success()

        runCatching { ReminderNotifier.showOpenHabits(applicationContext, data.habits) }
        Reminders.scheduleNext(
            context = applicationContext,
            hour = settings.reminderHour,
            minute = settings.reminderMinute,
        )
        return Result.success()
    }
}

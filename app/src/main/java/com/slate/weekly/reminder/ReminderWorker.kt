package com.slate.weekly.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.slate.weekly.data.SlateStore
import kotlinx.coroutines.flow.first

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val data = runCatching { SlateStore(applicationContext).data.first() }
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

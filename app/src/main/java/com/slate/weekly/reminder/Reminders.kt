package com.slate.weekly.reminder

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Reminders {

    const val CHANNEL_ID = "slate_reminder"
    private const val WORK_NAME = "slate_reminder_work"

    fun schedule(context: Context, hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        var next = now
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)
        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }
        val delayMinutes = Duration.between(now, next).toMinutes().coerceAtLeast(1)

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}

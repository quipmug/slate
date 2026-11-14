package com.slate.weekly.reminder

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object Reminders {

    const val CHANNEL_ID = "slate_reminder"
    private const val WORK_NAME = "slate_reminder_work"

    fun schedule(context: Context, hour: Int, minute: Int) {
        enqueue(context, ExistingWorkPolicy.REPLACE, hour, minute)
    }

    internal fun ensureScheduled(context: Context, hour: Int, minute: Int) {
        enqueue(context, ExistingWorkPolicy.KEEP, hour, minute)
    }

    internal fun scheduleNext(context: Context, hour: Int, minute: Int) {
        enqueue(context, ExistingWorkPolicy.APPEND_OR_REPLACE, hour, minute)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(WORK_NAME)
    }

    private fun enqueue(
        context: Context,
        policy: ExistingWorkPolicy,
        hour: Int,
        minute: Int,
    ) {
        val now = ZonedDateTime.now()
        val next = nextReminder(now, hour, minute)
        val delay = Duration.between(now, next).toMillis().coerceAtLeast(1_000L)

        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            WORK_NAME,
            policy,
            request,
        )
    }

    internal fun nextReminder(now: ZonedDateTime, hour: Int, minute: Int): ZonedDateTime {
        val target = now.toLocalDate()
            .atTime(hour.coerceIn(0, 23), minute.coerceIn(0, 59))
            .atZone(now.zone)
        return if (target.isAfter(now)) target else target.plusDays(1)
    }
}

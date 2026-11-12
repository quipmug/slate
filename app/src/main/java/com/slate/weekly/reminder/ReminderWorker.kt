package com.slate.weekly.reminder

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.slate.weekly.MainActivity
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.data.SlateStore
import com.slate.weekly.domain.weekChecks
import com.slate.weekly.util.startOfWeek
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val data = runCatching { SlateStore(applicationContext).data.first() }
            .getOrNull()
            ?: return Result.success()
        val settings = data.settings
        if (!settings.reminderOn) return Result.success()

        runCatching { showIfNeeded(data.habits) }
        Reminders.scheduleNext(
            context = applicationContext,
            hour = settings.reminderHour,
            minute = settings.reminderMinute,
        )
        return Result.success()
    }

    private fun showIfNeeded(habits: List<Habit>) {
        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val monday = today.startOfWeek()

        val open = habits.filter { habit ->
            todayEpoch >= habit.createdDay &&
                todayEpoch !in habit.checks &&
                weekChecks(habit, monday) < habit.target
        }
        if (open.isEmpty()) return
        if (!notificationsAllowed()) return

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val text = if (open.size == 1) {
            applicationContext.getString(R.string.reminder_one_open, open.first().name)
        } else {
            applicationContext.resources.getQuantityString(
                R.plurals.still_open_n,
                open.size,
                open.size,
            )
        }

        val notification = NotificationCompat.Builder(applicationContext, Reminders.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_note)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        NotificationManagerCompat.from(applicationContext).let { manager ->
            try {
                manager.notify(NOTIFICATION_ID, notification)
            } catch (_: SecurityException) {
                // revoked between the check above and this call
            }
        }
    }

    private fun notificationsAllowed(): Boolean {
        if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            return false
        }
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }

    private companion object {
        const val NOTIFICATION_ID = 11
    }
}

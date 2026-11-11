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
import com.slate.weekly.data.SlateStore
import com.slate.weekly.util.startOfWeek
import com.slate.weekly.util.weekChecks
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class ReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val habits = SlateStore(applicationContext).habits.first()
            .filter { !it.archived }
        if (habits.isEmpty()) return Result.success()

        val today = LocalDate.now()
        val todayEpoch = today.toEpochDay()
        val monday = today.startOfWeek()

        val open = habits.filter { habit ->
            todayEpoch >= habit.createdDay &&
                todayEpoch !in habit.checks &&
                weekChecks(habit, monday) < habit.target
        }
        if (open.isEmpty()) return Result.success()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return Result.success()
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val text = applicationContext.resources.getQuantityString(
            R.plurals.still_open_n,
            open.size,
            open.size,
        )

        val notification = NotificationCompat.Builder(applicationContext, Reminders.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_note)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(11, notification)
        } catch (_: SecurityException) {
            return Result.success()
        }
        return Result.success()
    }
}

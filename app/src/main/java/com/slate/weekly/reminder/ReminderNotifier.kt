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
import com.slate.weekly.MainActivity
import com.slate.weekly.R
import com.slate.weekly.data.Habit
import com.slate.weekly.domain.openHabits
import java.time.LocalDate

internal object ReminderNotifier {

    private const val NOTIFICATION_ID = 11

    fun showOpenHabits(context: Context, habits: List<Habit>, today: LocalDate = LocalDate.now()): Boolean {
        val open = openHabits(habits, today)
        if (open.isEmpty()) return false

        val text = if (open.size == 1) {
            context.getString(R.string.reminder_one_open, open.first().name)
        } else {
            context.resources.getQuantityString(
                R.plurals.still_open_n,
                open.size,
                open.size,
            )
        }
        return post(context, text)
    }

    fun preview(context: Context): Boolean =
        post(context, context.getString(R.string.reminder_preview_text))

    private fun post(context: Context, text: String): Boolean {
        if (!notificationsAllowed(context)) return false

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = NotificationCompat.Builder(context, Reminders.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_note)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setShowWhen(false)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        return try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
            true
        } catch (_: SecurityException) {
            // revoked between the check and the call
            false
        }
    }

    private fun notificationsAllowed(context: Context): Boolean {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            return false
        }
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
    }
}

package com.slate.weekly

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.slate.weekly.data.SlateStore
import com.slate.weekly.reminder.Reminders
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SlateApp : Application() {

    @Inject
    lateinit var store: SlateStore

    override fun onCreate() {
        super.onCreate()
        createReminderChannel()
    }

    private fun createReminderChannel() {
        val channel = NotificationChannel(
            Reminders.CHANNEL_ID,
            getString(R.string.reminder_channel),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.reminder_channel_desc)
        }
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}

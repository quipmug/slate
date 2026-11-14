package com.slate.weekly.reminder

import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class RemindersTest {

    private val zone = ZoneId.of("Europe/Moscow")

    @Test
    fun `next reminder stays today when the time is ahead`() {
        val now = ZonedDateTime.of(2026, 11, 11, 19, 30, 0, 0, zone)
        val next = Reminders.nextReminder(now, 20, 0)
        assertEquals(ZonedDateTime.of(2026, 11, 11, 20, 0, 0, 0, zone), next)
    }

    @Test
    fun `next reminder rolls to tomorrow when the time has passed`() {
        val now = ZonedDateTime.of(2026, 11, 11, 20, 1, 0, 0, zone)
        val next = Reminders.nextReminder(now, 20, 0)
        assertEquals(ZonedDateTime.of(2026, 11, 12, 20, 0, 0, 0, zone), next)
    }

    @Test
    fun `next reminder is strictly in the future`() {
        val now = ZonedDateTime.of(2026, 11, 11, 20, 0, 0, 0, zone)
        val next = Reminders.nextReminder(now, 20, 0)
        assertEquals(ZonedDateTime.of(2026, 11, 12, 20, 0, 0, 0, zone), next)
    }
}

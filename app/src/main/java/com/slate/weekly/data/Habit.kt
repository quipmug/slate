package com.slate.weekly.data

import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    val id: String,
    val name: String,
    val target: Int,
    val createdDay: Long,
    val checks: Set<Long> = emptySet(),
    val archived: Boolean = false,
)

@Serializable
data class SlateSettings(
    val theme: String = "auto",
    val reminderOn: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
)

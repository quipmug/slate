package com.slate.weekly.data

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Habit(
    val id: String,
    val name: String,
    val target: Int,
    val createdDay: Long,
    val checks: Set<Long> = emptySet(),
) {
    fun toEntity(): HabitEntity =
        HabitEntity(id, name, target, createdDay)
}

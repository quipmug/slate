package com.slate.weekly.data

import kotlinx.serialization.Serializable

@Serializable
data class Habit(
    val id: String,
    val name: String,
    val target: Int,
    val createdDay: Long,
    val checks: Set<Long> = emptySet(),
)

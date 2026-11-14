package com.slate.weekly.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_checks",
    primaryKeys = ["habit_id", "day"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habit_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitCheck(
    @ColumnInfo(name = "habit_id") val habitId: String,
    @ColumnInfo(name = "day") val day: Long,
)

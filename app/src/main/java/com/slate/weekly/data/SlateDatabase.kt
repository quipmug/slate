package com.slate.weekly.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [HabitEntity::class, HabitCheck::class],
    version = 1,
    exportSchema = true,
)
abstract class SlateDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao
}

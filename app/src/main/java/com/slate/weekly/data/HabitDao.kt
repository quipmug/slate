package com.slate.weekly.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY created_day ASC, name ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habit_checks")
    fun getAllChecks(): Flow<List<HabitCheck>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: String): HabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(vararg habits: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()

    @Query("SELECT day FROM habit_checks WHERE habit_id = :id")
    suspend fun getCheckDays(id: String): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: HabitCheck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecks(vararg checks: HabitCheck)

    @Query("DELETE FROM habit_checks WHERE habit_id = :habitId AND day = :day")
    suspend fun deleteCheckForHabit(habitId: String, day: Long)

    @Query("DELETE FROM habit_checks WHERE habit_id = :habitId")
    suspend fun deleteAllChecksForHabit(habitId: String)

    @Transaction
    suspend fun upsertHabitWithChecks(habit: HabitEntity, checks: Set<Long>) {
        insertHabit(habit)
        deleteAllChecksForHabit(habit.id)
        if (checks.isNotEmpty()) {
            insertChecks(*checks.map { HabitCheck(habit.id, it) }.toTypedArray())
        }
    }

    @Transaction
    suspend fun toggleCheck(habitId: String, day: Long) {
        if (day in getCheckDays(habitId)) {
            deleteCheckForHabit(habitId, day)
        } else {
            insertCheck(HabitCheck(habitId, day))
        }
    }
}

package com.slate.weekly.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

data class HabitWithChecks(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "habit_id"
    )
    val checks: List<HabitCheck>,
)

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY created_day ASC, name ASC")
    fun getAllHabitsWithChecks(): Flow<List<HabitWithChecks>>

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
    fun getChecksForHabit(id: String): Flow<Set<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: HabitCheck)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecks(vararg checks: HabitCheck)

    @Delete
    suspend fun deleteCheck(check: HabitCheck)

    @Query("DELETE FROM habit_checks WHERE habit_id = :id AND day = :day")
    suspend fun deleteCheckForHabit(habitId: String, day: Long)

    @Query("DELETE FROM habit_checks WHERE habit_id = :id")
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
        val existing = getChecksForHabit(habitId).first()
        if (day in existing) {
            deleteCheckForHabit(habitId, day)
        } else {
            insertCheck(HabitCheck(habitId, day))
        }
    }
}

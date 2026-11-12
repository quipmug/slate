package com.slate.weekly.data

object HabitRules {

    const val MIN_TARGET = 1
    const val MAX_TARGET = 7
    const val DEFAULT_TARGET = 3
    const val MAX_NAME_LENGTH = 48

    fun cleanName(raw: String): String = raw.trim().take(MAX_NAME_LENGTH)

    fun cleanTarget(value: Int): Int = value.coerceIn(MIN_TARGET, MAX_TARGET)

    fun normalize(habits: List<Habit>, today: Long = Long.MAX_VALUE): List<Habit> {
        val seen = HashSet<String>(habits.size)
        return habits.mapNotNull { habit ->
            val id = habit.id.trim()
            val name = cleanName(habit.name)
            if (id.isEmpty() || name.isEmpty() || !seen.add(id)) {
                null
            } else {
                val createdDay = habit.createdDay.coerceAtMost(today)
                habit.copy(
                    id = id,
                    name = name,
                    target = cleanTarget(habit.target),
                    createdDay = createdDay,
                    checks = habit.checks.filterTo(LinkedHashSet()) {
                        it in createdDay..today
                    },
                )
            }
        }
    }

    fun toggled(habit: Habit, day: Long, today: Long): Habit =
        if (day < habit.createdDay || day > today) {
            habit
        } else {
            habit.copy(
                checks = if (day in habit.checks) habit.checks - day else habit.checks + day,
            )
        }
}

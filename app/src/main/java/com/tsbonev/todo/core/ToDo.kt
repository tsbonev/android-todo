package com.tsbonev.todo.core

import com.tsbonev.todo.adapter.room.ToDoEntity
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.absoluteValue

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDo(val id: String,
                val content: String,
                val dueDate: String,
                val createdOn: LocalDateTime,
                val completed: Boolean = false)
{
    constructor(entity: ToDoEntity) : this(
        entity.id,
        entity.content,
        entity.dueDate?.convertDayToRange(LocalDateTime.now()) ?: "",
        entity.createdOn,
        entity.completed
    )
}

private fun ChronoUnit.asSuffix(): String {
    return when(this) {
        ChronoUnit.MINUTES -> "min"
        ChronoUnit.HOURS -> "h"
        ChronoUnit.DAYS -> "d"
        ChronoUnit.WEEKS -> "w"
        ChronoUnit.MONTHS -> "mon"
        ChronoUnit.YEARS -> "y"

        else -> ""
    }
}

/**
 * Converts a date representing a due date into a pair of adjacent [ChronoUnit] units.
 */
fun LocalDateTime.convertDayToRange(current: LocalDateTime) : String {
    val differences = getListWithDifferences(current, this)

    val differencePair = differences.plus(" ").zipWithNext().firstOrNull { it.first.isNotBlank() }

    return if(differencePair == null) ""
    else "${differencePair.first} ${differencePair.second}".trim()
}


/**
 * Fills a list with blanks or differences in [ChronoUnit] between two dates.
 */
private fun getListWithDifferences(start: LocalDateTime, end: LocalDateTime) : List<String> {
    val resultList = mutableListOf<String>()
    val relativeStart = if(start.isBefore(end)) start else end
    val relativeEnd = if(relativeStart == start) end else start
    listOf(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.WEEKS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES).forEach {
        val diff = getDifference(it, relativeStart, relativeEnd)
        if(diff != null) resultList.add(diff) else resultList.add(" ")
    }

    return resultList
}

/**
 * Returns the proper numeric and suffix for the difference in a given [ChronoUnit] or null if there is no difference.
 */
private fun getDifference(unit: ChronoUnit, start: LocalDateTime, end: LocalDateTime): String? {
    val accountedForStart = start.accountForPrevious(unit, end)
    val diff = unit.between(accountedForStart, end).absoluteValue

    return if(diff != 0L) "$diff${unit.asSuffix()}" else null
}

/**
 * Accounts for the previous difference by removing the upper-class [ChronoUnit] difference.
 */
private fun LocalDateTime.accountForPrevious(unit: ChronoUnit, end: LocalDateTime): LocalDateTime {
    return when (unit) {
        ChronoUnit.MINUTES -> this.minusHours(ChronoUnit.HOURS.between(end, this))
        ChronoUnit.HOURS -> this.minusDays(ChronoUnit.DAYS.between(end, this))
        ChronoUnit.DAYS -> this.minusWeeks(ChronoUnit.WEEKS.between(end, this))
        ChronoUnit.WEEKS -> this.minusMonths(ChronoUnit.MONTHS.between(end, this))
        ChronoUnit.MONTHS -> this.minusYears(ChronoUnit.YEARS.between(end, this))

        else -> this
    }
}

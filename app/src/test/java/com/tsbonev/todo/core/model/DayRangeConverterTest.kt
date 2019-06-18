package com.tsbonev.todo.core.model
import com.tsbonev.todo.core.ToDo
import com.tsbonev.todo.core.ToDoStatus
import org.hamcrest.CoreMatchers.`is` as Is
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class DayRangeConverterTest {

    private val initialDate = LocalDateTime.ofEpochSecond(1L, 1, ZoneOffset.UTC)

    @Test
    fun `Convert day range when current is null`() {
        val current = LocalDateTime.now()
        val toDo = ToDo("::id::", "::content::", null, current, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, current)

        assertThat(toDoViewModel.dueDate, Is(""))
    }

    @Test
    fun `Convert day range when current behind date`() {
        val current = initialDate.plusHours(1)
        val date = initialDate
        val toDo = ToDo("::id::", "::content::", date, date, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, current)

        assertThat(toDoViewModel.dueDate, Is("1h"))
    }


    @Test
    fun `Convert day range minute only`() {
        val dueDate = initialDate.plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("12min"))
    }

    @Test
    fun `Convert day range hour and minute`() {
        val dueDate = initialDate.plusHours(2).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("2h 12min"))
    }

    @Test
    fun `Convert day range day and hour`() {
        val dueDate = initialDate.plusDays(2).plusHours(2).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("2d 2h"))
    }

    @Test
    fun `Convert day range day and no hours`() {
        val dueDate = initialDate.plusDays(2).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("2d"))
    }

    @Test
    fun `Convert day range weeks and days`() {
        val dueDate = initialDate.plusWeeks(1).plusDays(2).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("1w 2d"))
    }

    @Test
    fun `Convert day range weeks and no days`() {
        val dueDate = initialDate.plusWeeks(1).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("1w"))
    }

    @Test
    fun `Convert day range months and weeks`() {
        val dueDate = initialDate.plusMonths(2).plusWeeks(1).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("2mon 1w"))
    }

    @Test
    fun `Convert day range months and no weeks`() {
        val dueDate = initialDate.plusMonths(2).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("2mon"))
    }

    @Test
    fun `Convert day range years and months`() {
        val dueDate = initialDate.plusYears(1).plusMonths(2).plusWeeks(1).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("1y 2mon"))
    }

    @Test
    fun `Convert day range years and no months`() {
        val dueDate = initialDate.plusYears(1).plusWeeks(1).plusMinutes(12)
        val toDo = ToDo("::id::", "::content::", dueDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is("1y"))
    }

    @Test
    fun `Convert day range when no difference`() {
        val toDo = ToDo("::id::", "::content::", initialDate, initialDate, ToDoStatus.CURRENT)

        val toDoViewModel = ToDoViewModel(toDo, initialDate)

        assertThat(toDoViewModel.dueDate, Is(""))
    }
}
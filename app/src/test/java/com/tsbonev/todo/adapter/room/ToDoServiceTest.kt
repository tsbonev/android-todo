package com.tsbonev.todo.adapter.room

import com.tsbonev.todo.core.*
import com.tsbonev.todo.helper.expecting
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.jmock.AbstractExpectations.returnValue
import org.jmock.integration.junit4.JUnitRuleMockery
import org.jmock.lib.concurrent.Synchroniser
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDateTime
import org.hamcrest.CoreMatchers.`is` as Is

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoServiceTest {
    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    init {
        context.setThreadingPolicy(Synchroniser())
    }

    private val idGenerator = context.mock(IdGenerator::class.java)

    private val toDoDao = context.mock(ToDoDao::class.java)

    private val service = ToDoService(toDoDao, idGenerator)

    private val date = LocalDateTime.now()

    private val basicEntity = ToDoEntity(
        "::id::", "::content::",
        date, date, false
    )


    @Test
    fun `Happy path`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))

            oneOf(toDoDao).add(basicEntity)

            oneOf(toDoDao).getById("::id::")
            will(returnValue(basicEntity))

            oneOf(toDoDao).update(basicEntity.copy(completed = true))

            oneOf(toDoDao).getById("::id::")
            will(returnValue(basicEntity.copy(completed = true)))
        }

        val createdToDo = service.add(AddToDoRequest("::content::", date, date))

        service.complete("::id::")

        val foundToDo = service.getById("::id::")

        assertThat(foundToDo, Is(notNullValue()))
        assertThat(foundToDo, Is(createdToDo.copy(status = ToDoStatus.COMPLETED)))
    }

    @Test
    fun `Get todo when not found`() {

        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        val toDo = service.getById("::id::")

        assertThat(toDo, Is(nullValue()))
    }


    @Test
    fun `Edit todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))

            oneOf(toDoDao).add(basicEntity)

            oneOf(toDoDao).getById("::id::")
            will(returnValue(basicEntity))

            oneOf(toDoDao).update(basicEntity.copy(content = "::newContent::", dueDate = null))
        }

        val addedToDo = service.add(AddToDoRequest("::content::", date, date))

        val updatedToDo = service.edit(EditToDoRequest("::id::", "::newContent::", null))

        assertThat(updatedToDo, Is(addedToDo.copy(content = "::newContent::", dueDate = null)))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Edit todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        service.edit(EditToDoRequest("::id::", "::newContent::", null))
    }

    @Test
    fun `Remove todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))

            oneOf(toDoDao).add(basicEntity)

            oneOf(toDoDao).getById("::id::")
            will(returnValue(basicEntity))

            oneOf(toDoDao).delete(basicEntity)
        }

        val addedToDo = service.add(AddToDoRequest("::content::", date, date))

        val removedToDo = service.remove("::id::")
        assertThat(addedToDo, Is(removedToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Remove todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        service.remove("::id::")
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Complete todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        service.complete("::id::")
    }

    @Test
    fun `Revert todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))

            oneOf(toDoDao).add(basicEntity)

            oneOf(toDoDao).getById("::id::")
            will(returnValue(basicEntity))

            oneOf(toDoDao).update(basicEntity.copy(completed = true))

            oneOf(toDoDao).getById("::id::")
            will(returnValue(basicEntity.copy(completed = true)))

            oneOf(toDoDao).update(basicEntity)
        }

        service.add(AddToDoRequest("::content::", date, date))
        service.complete("::id::")

        val revertedToDo = service.revert("::id::")

        assertThat(revertedToDo.status, Is(ToDoStatus.CURRENT))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Revert todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        service.revert("::id::")
    }

    @Test
    fun `Get all current todos`() {
        context.expecting {
            oneOf(toDoDao).getAllCurrent(date)
            will(returnValue(listOf(basicEntity, basicEntity)))
        }

        val todos = service.getAllCurrent(date)

        assertThat(todos, Is(listOf(basicEntity.toDomain(), basicEntity.toDomain())))
    }

    @Test
    fun `Get all current todos when empty`() {
        context.expecting {
            oneOf(toDoDao).getAllCurrent(date)
            will(returnValue(listOf<ToDoEntity>()))
        }

        val todos = service.getAllCurrent(date)

        assertThat(todos, Is(listOf()))
    }

    @Test
    fun `Get all overdue todos`() {
        context.expecting {
            oneOf(toDoDao).getAllOverdue(date)
            will(returnValue(listOf(basicEntity, basicEntity)))
        }

        val todos = service.getAllOverdue(date)

        assertThat(todos, Is(listOf(basicEntity.toDomain(), basicEntity.toDomain())))
    }

    @Test
    fun `Get all overdue todos when empty`() {
        context.expecting {
            oneOf(toDoDao).getAllOverdue(date)
            will(returnValue(listOf<ToDoEntity>()))
        }

        val todos = service.getAllOverdue(date)

        assertThat(todos, Is(listOf()))
    }

    @Test
    fun `Get all completed todos`() {
        context.expecting {
            oneOf(toDoDao).getAllCompleted()
            will(returnValue(listOf(basicEntity, basicEntity)))
        }

        val todos = service.getAllCompleted()

        assertThat(todos, Is(listOf(basicEntity.toDomain(), basicEntity.toDomain())))
    }

    @Test
    fun `Get all completed todos when empty`() {
        context.expecting {
            oneOf(toDoDao).getAllCompleted()
            will(returnValue(listOf<ToDoEntity>()))
        }

        val todos = service.getAllCompleted()

        assertThat(todos, Is(listOf()))
    }
}
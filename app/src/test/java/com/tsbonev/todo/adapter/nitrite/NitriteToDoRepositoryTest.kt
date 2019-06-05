package com.tsbonev.todo.adapter.nitrite

import com.tsbonev.todo.core.*
import com.tsbonev.todo.helper.expecting
import org.dizitart.kno2.nitrite
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.Matchers.containsInAnyOrder
import org.jmock.AbstractExpectations.returnValue
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.hamcrest.CoreMatchers.`is` as Is
import org.junit.Assert.assertThat

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class NitriteToDoRepositoryTest {

    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    private val db = nitrite { }

    private val date = LocalDateTime.ofInstant(Instant.ofEpochSecond(1), ZoneOffset.UTC)

    private val idGenerator = context.mock(IdGenerator::class.java)

    private val repo = NitriteToDoRepository(db, "ToDoTest", idGenerator)

    @Test
    fun `Happy path`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.add(AddToDoRequest( "::content::", date, date))

        repo.complete("::id::")

        val foundToDo = repo.getById("::id::")

        assertThat(foundToDo, Is(notNullValue()))
        assertThat(foundToDo, Is(createdToDo.copy(status = ToDoStatus.COMPLETED)))
    }

    @Test
    fun `Edit todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        repo.add(AddToDoRequest( "::content::", date, date))

        val editedToDo = repo.edit(EditToDoRequest("::id::", "::newContent::", date.plusDays(1)))

        val foundToDo = repo.getById("::id::")

        assertThat(foundToDo, Is(notNullValue()))
        assertThat(foundToDo, Is(editedToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Edit todo when not found`() {
        repo.edit(EditToDoRequest("::id::", "::newContent::", date.plusDays(1)))
    }

    @Test
    fun `Revert todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.add(AddToDoRequest( "::content::", date, date))

        repo.complete("::id::")
        repo.revert("::id::")

        val foundToDo = repo.getById("::id::")

        assertThat(foundToDo, Is(notNullValue()))
        assertThat(foundToDo, Is(createdToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Revert todo when not found`() {
        repo.remove("::id::")
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Complete todo when not found`() {
        repo.complete("::id::")
    }

    @Test
    fun `Find todo by id when none present`() {
        val todo = repo.getById("::id::")

        assertThat(todo, Is(nullValue()))
    }

    @Test
    fun `Search todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id-1::"))

            oneOf(idGenerator).generateId()
            will(returnValue("::id-2::"))
        }

        val createdToDo1 = repo.add(AddToDoRequest("::content::", date, date))
        val createdToDo2 = repo.add(AddToDoRequest("::content:: banana", date, date))

        val toDos1 = repo.searchByContent("::content::", SortOrder.ASCENDING_DUE_DATE, Cursor())

        val toDos2= repo.searchByContent("banana", SortOrder.ASCENDING_DUE_DATE, Cursor())

        assertThat(toDos1, Is(containsInAnyOrder(createdToDo1, createdToDo2)))
        assertThat(toDos2, Is(listOf(createdToDo2)))
    }

    @Test
    fun `Search todo when none found`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id-1::"))

            oneOf(idGenerator).generateId()
            will(returnValue("::id-2::"))
        }

        repo.add(AddToDoRequest( "::content::", date, date))
        repo.add(AddToDoRequest( "::content::", date, date))

        val toDos = repo.searchByContent("::nonmatch::", SortOrder.ASCENDING_DUE_DATE, Cursor())

        assertThat(toDos.isEmpty(), Is(true))
    }

    @Test
    fun `Remove todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.add(AddToDoRequest( "::content::", date, date))

        val removedToDo = repo.remove("::id::")

        val foundToDo = repo.getById("::id::")

        assertThat(foundToDo, Is(nullValue()))
        assertThat(removedToDo, Is(createdToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Remove todo when not found`() {
        repo.remove("::id::")
    }


    @Test
    fun `Get overdue todos`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.add(AddToDoRequest("::content::", date, date))

        val overdueToDos = repo.getAllOverdue(Cursor(), date.plusDays(1))

        assertThat(overdueToDos.contains(createdToDo), Is(true))
    }

    @Test
    fun `Get overdue todos when completed`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        repo.add(AddToDoRequest( "::content::", date, date))

        repo.complete("::id::")

        val overdueToDos = repo.getAllOverdue(Cursor(), date.plusDays(1))

        assertThat(overdueToDos.isEmpty(), Is(true))
    }

    @Test
    fun `Get current todos`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.add(AddToDoRequest( "::content::", date, date.plusDays(1)))

        val overdueToDos = repo.getAllCurrent(Cursor(), date)

        assertThat(overdueToDos.contains(createdToDo), Is(true))
    }

    @Test
    fun `Get current todos when completed`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        repo.add(AddToDoRequest("::content::", date, date.plusDays(1)))

        repo.complete("::id::")

        val overdueToDos = repo.getAllCurrent(Cursor(), date)

        assertThat(overdueToDos.isEmpty(), Is(true))
    }
}

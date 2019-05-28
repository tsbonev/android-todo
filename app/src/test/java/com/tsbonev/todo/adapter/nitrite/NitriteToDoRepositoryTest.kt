package com.tsbonev.todo.adapter.nitrite

import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.EditToDoRequest
import com.tsbonev.todo.core.IdGenerator
import com.tsbonev.todo.core.ToDoNotFoundException
import com.tsbonev.todo.helper.expecting
import org.dizitart.kno2.nitrite
import org.jmock.AbstractExpectations.returnValue
import org.jmock.integration.junit4.JUnitRuleMockery
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
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

        val createdToDo = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        repo.completeToDo("::id::")

        val foundToDo = repo.getToDoById("::id::")

        assertThat(foundToDo.isPresent, Is(true))
        assertThat(foundToDo.get(), Is(createdToDo.copy(completed = true)))
    }

    @Test
    fun `Edit todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        val editedToDo = repo.editToDo(EditToDoRequest("::id::",
            "::newTitle::", "::newContent::", setOf("::newTags::"), date.plusDays(1)))

        val foundToDo = repo.getToDoById("::id::")

        assertThat(foundToDo.isPresent, Is(true))
        assertThat(foundToDo.get(), Is(editedToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Edit todo when not found`() {
        repo.editToDo(EditToDoRequest("::id::",
            "::newTitle::", "::newContent::", setOf("::newTags::"), date.plusDays(1)))
    }

    @Test
    fun `Revert todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        repo.completeToDo("::id::")
        repo.revertToDo("::id::")

        val foundToDo = repo.getToDoById("::id::")

        assertThat(foundToDo.isPresent, Is(true))
        assertThat(foundToDo.get(), Is(createdToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Revert todo when not found`() {
        repo.removeToDo("::id::")
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Complete todo when not found`() {
        repo.completeToDo("::id::")
    }

    @Test
    fun `Find todo by id when none present`() {
        val todo = repo.getToDoById("::id::")

        assertThat(todo.isPresent, Is(false))
    }

    @Test
    fun `Find todo by single tags`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        val toDos = repo.findToDosByTags(setOf("::tag-1::"))

        assertThat(toDos.contains(createdToDo), Is(true))
    }

    @Test
    fun `Find todo by multiple tags`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id-1::"))

            oneOf(idGenerator).generateId()
            will(returnValue("::id-2::"))
        }

        val createdToDo1 = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))
        val createdToDo2 = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-2::")))

        val toDos = repo.findToDosByTags(setOf("::tag-1::", "::tag-2::"))

        assertThat(toDos.contains(createdToDo1), Is(true))
        assertThat(toDos.contains(createdToDo2), Is(true))
    }

    @Test
    fun `Find todo by tags when not found`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id-1::"))

            oneOf(idGenerator).generateId()
            will(returnValue("::id-2::"))
        }

        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))
        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-2::")))

        val toDos = repo.findToDosByTags(setOf("::tag-3::"))

        assertThat(toDos.isEmpty(), Is(true))
    }

    @Test
    fun `Search todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id-1::"))

            oneOf(idGenerator).generateId()
            will(returnValue("::id-2::"))
        }

        val createdToDo1 = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))
        val createdToDo2 = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-2::", "::banana::")))

        val toDos1 = repo.searchToDos("::content:: ::title:: ::tag-1::")

        val toDos2= repo.searchToDos("banana")

        assertThat(toDos1.contains(createdToDo1), Is(true))
        assertThat(toDos1.contains(createdToDo2), Is(true))

        assertThat(toDos2, Is(listOf(createdToDo2)))
    }

    @Test
    fun `Search todo when noe found`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id-1::"))

            oneOf(idGenerator).generateId()
            will(returnValue("::id-2::"))
        }

        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))
        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-2::", "::banana::")))

        val toDos = repo.searchToDos("::nonmatch::")

        assertThat(toDos.isEmpty(), Is(true))
    }

    @Test
    fun `Remove todo`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        val removedToDo = repo.removeToDo("::id::")

        val foundToDo = repo.getToDoById("::id::")

        assertThat(foundToDo.isPresent, Is(false))
        assertThat(removedToDo, Is(createdToDo))
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Remove todo when not found`() {
        repo.removeToDo("::id::")
    }


    @Test
    fun `Get expired todos`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        val expiredToDos = repo.getExpiredToDos(date.plusDays(1))

        assertThat(expiredToDos.contains(createdToDo), Is(true))
    }

    @Test
    fun `Get expired todos when completed`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date, setOf("::tag-1::", "::tag-2::")))

        repo.completeToDo("::id::")

        val expiredToDos = repo.getExpiredToDos(date.plusDays(1))

        assertThat(expiredToDos.isEmpty(), Is(true))
    }

    @Test
    fun `Get active todos`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        val createdToDo = repo.addToDo(AddToDoRequest("::title::", "::content::", date, date.plusDays(1), setOf("::tag-1::", "::tag-2::")))

        val expiredToDos = repo.getActiveToDos(date)

        assertThat(expiredToDos.contains(createdToDo), Is(true))
    }

    @Test
    fun `Get active todos when completed`() {
        context.expecting {
            oneOf(idGenerator).generateId()
            will(returnValue("::id::"))
        }

        repo.addToDo(AddToDoRequest("::title::", "::content::", date, date.plusDays(1), setOf("::tag-1::", "::tag-2::")))

        repo.completeToDo("::id::")

        val expiredToDos = repo.getActiveToDos(date)

        assertThat(expiredToDos.isEmpty(), Is(true))
    }
}

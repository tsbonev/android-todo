package com.tsbonev.todo.adapter.room

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tsbonev.todo.core.*
import com.tsbonev.todo.helper.expecting
import com.tsbonev.todo.helper.observeOnce
import kotlinx.coroutines.runBlocking
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
import org.junit.rules.TestRule



/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoServiceTest {
    @Rule
    @JvmField
    val context: JUnitRuleMockery = JUnitRuleMockery()

    @Rule
    @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

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

        runBlocking {
            val createdToDo = service.add(AddToDoRequest("::content::", date, date))

            service.complete("::id::")

            val foundToDo = service.getById("::id::")!!

            assertThat(foundToDo, Is(notNullValue()))
            assertThat(foundToDo, Is(createdToDo.copy(completed = true)))
        }
    }

    @Test
    fun `Get todo when not found`() {

        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        runBlocking {
            val toDo = service.getById("::id::")

            assertThat(toDo, Is(nullValue()))
        }
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


        runBlocking {
            val addedToDo = service.add(AddToDoRequest("::content::", date, date))

            val updatedToDo = service.edit(EditToDoRequest("::id::", "::newContent::", null))

            assertThat(updatedToDo, Is(addedToDo.copy(content = "::newContent::", dueDate = "")))
        }
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Edit todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        runBlocking {
            service.edit(EditToDoRequest("::id::", "::newContent::", null))
        }
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

        runBlocking {
            val addedToDo = service.add(AddToDoRequest("::content::", date, date))

            val removedToDo = service.remove("::id::")
            assertThat(addedToDo, Is(removedToDo))
        }
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Remove todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        runBlocking {
            service.remove("::id::")
        }
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Complete todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }

        runBlocking {
            service.complete("::id::")
        }
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

        runBlocking {
            service.add(AddToDoRequest("::content::", date, date))
            service.complete("::id::")

            val revertedToDo = service.revert("::id::")
            assertThat(revertedToDo.completed, Is(false))
        }
    }

    @Test(expected = ToDoNotFoundException::class)
    fun `Revert todo when not found`() {
        context.expecting {
            oneOf(toDoDao).getById("::id::")
            will(returnValue(null))
        }


        runBlocking {
            service.revert("::id::")
        }
    }

    @Test
    fun `Get all current todos`() {
        val data = MutableLiveData<List<ToDoEntity>>()

        data.value = listOf(basicEntity, basicEntity)

        context.expecting {
            oneOf(toDoDao).getAllCurrent(date)
            will(returnValue(data))
        }

        runBlocking {
            service.getAllCurrent(date).observeOnce {
                assertThat(it, Is(listOf(ToDo(basicEntity), ToDo(basicEntity))))
            }
        }
    }

    @Test
    fun `Get all current todos when empty`() {
        val data = MutableLiveData<List<ToDoEntity>>()
        data.value = listOf()

        context.expecting {
            oneOf(toDoDao).getAllCurrent(date)
            will(returnValue(data))
        }

        runBlocking {
            service.getAllCurrent(date).observeOnce {
                assertThat(it, Is(listOf()))
            }
        }
    }

    @Test
    fun `Get all overdue todos`() {
        val data = MutableLiveData<List<ToDoEntity>>()

        data.value = listOf(basicEntity, basicEntity)

        context.expecting {
            oneOf(toDoDao).getAllOverdue(date)
            will(returnValue(data))
        }

        runBlocking {
            service.getAllOverdue(date).observeOnce {
                assertThat(it, Is(listOf(ToDo(basicEntity), ToDo(basicEntity))))
            }
        }
    }

    @Test
    fun `Get all overdue todos when empty`() {
        val data = MutableLiveData<List<ToDoEntity>>()
        data.value = listOf()


        context.expecting {
            oneOf(toDoDao).getAllOverdue(date)
            will(returnValue(data))
        }

        runBlocking {
            service.getAllOverdue(date).observeOnce {
                assertThat(it, Is(listOf()))
            }
        }
    }

    @Test
    fun `Get all completed todos`() {
        val data = MutableLiveData<List<ToDoEntity>>()

        data.value = listOf(basicEntity, basicEntity)

        context.expecting {
            oneOf(toDoDao).getAllCompleted()
            will(returnValue(data))
        }

        runBlocking {
            service.getAllCompleted().observeOnce {
                assertThat(it, Is(listOf(ToDo(basicEntity), ToDo(basicEntity))))
            }
        }
    }

    @Test
    fun `Get all completed todos when empty`() {
        val data = MutableLiveData<List<ToDoEntity>>()
        data.value = listOf()


        context.expecting {
            oneOf(toDoDao).getAllCompleted()
            will(returnValue(data))
        }

        runBlocking {
            service.getAllCompleted().observeOnce {
                assertThat(it, Is(listOf()))
            }
        }
    }
}
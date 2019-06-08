package com.tsbonev.todo.adapter.room

import androidx.room.Room
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.`is` as Is
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.nullValue


/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
@RunWith(AndroidJUnit4::class)
class ToDoDaoTest {
    private lateinit var toDoDao: ToDoDao
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getInstrumentation().context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        toDoDao = db.toDoDao()
    }

    @After
    fun cleanUp() {
        db.close()
    }

    @Test
    fun `Insert and retrieve toDo`() {
        val time = LocalDateTime.now()
        val entity = ToDoEntity("::id::", "::content::", time, time, true)

        toDoDao.add(entity)

        val retrievedEntity = toDoDao.getById("::id::")

        assertThat(retrievedEntity, Is(notNullValue()))
        assertThat(retrievedEntity, Is(entity))
    }

    @Test
    fun `Fail to find toDo by id`() {
        val retrievedToDo = toDoDao.getById("::id::")

        assertThat(retrievedToDo, Is(nullValue()))
    }

    @Test
    fun `Delete toDo`() {
        val time = LocalDateTime.now()
        val entity = ToDoEntity("::id::", "::content::", time, time, true)

        toDoDao.delete(entity)

        val retrievedEntity = toDoDao.getById("::id::")

        assertThat(retrievedEntity, Is(nullValue()))
    }

    @Test
    fun `Update toDo`() {
        val time = LocalDateTime.now()
        val entity = ToDoEntity("::id::", "::content::", time, time, true)
        toDoDao.add(entity)

        toDoDao.update(entity.copy(content = "::newContent::"))

        val retrievedEntity = toDoDao.getById("::id::")

        assertThat(retrievedEntity, Is(notNullValue()))
        assertThat(retrievedEntity, Is(retrievedEntity!!.copy(content = "::newContent::")))
    }

    @Test
    fun `Find overdue toDos`() {
        val time = LocalDateTime.now()
        val entity = ToDoEntity("::id::", "::content::", time, time, false)

        toDoDao.add(entity)
        toDoDao.add(entity.copy(dueDate = null, id = "::id-1::"))
        toDoDao.add(entity.copy(dueDate = time.plusDays(3), id = "::id-2::"))
        toDoDao.add(entity.copy(completed = true, id = "::id-3::"))

        val retrievedEntities = toDoDao.getAllOverdue(time.plusDays(1))

        assertThat(retrievedEntities, Is(listOf(entity)))
    }

    @Test
    fun `Find current toDos`() {
        val time = LocalDateTime.now()
        val entity = ToDoEntity("::id::", "::content::", time, time, false)

        toDoDao.add(entity)
        toDoDao.add(entity.copy(dueDate = time.minusDays(3), id = "::id-1::"))
        toDoDao.add(entity.copy(completed = true, id = "::id-2::"))

        val retrievedEntities = toDoDao.getAllCurrent(time.minusDays(1))

        assertThat(retrievedEntities, Is(listOf(entity)))
    }

    @Test
    fun `Find completed toDos`() {
        val time = LocalDateTime.now()
        val entity = ToDoEntity("::id::", "::content::", time, time, true)

        toDoDao.add(entity)
        toDoDao.add(entity.copy(completed = false, id = "::id-1::"))

        val retrievedEntities = toDoDao.getAllCompleted()

        assertThat(retrievedEntities, Is(listOf(entity)))
    }
}
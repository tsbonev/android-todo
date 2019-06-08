package com.tsbonev.todo.adapter.room

import com.tsbonev.todo.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoService(
    private val toDoDao: ToDoDao,
    private val idGenerator: IdGenerator
) : ToDos {
    override fun add(request: AddToDoRequest): ToDo = runBlocking {
        val todo = CoroutineScope(IO).async {
            val entity = ToDoEntity(
                idGenerator.generateId(), request.content,
                request.dueDate, request.createdOn
            )

            toDoDao.add(entity)

            entity.toDomain()
        }

        todo.await()
    }

    override fun edit(request: EditToDoRequest): ToDo = runBlocking {
        val updatedEntity = CoroutineScope(IO).async {
            val entity = toDoDao.getById(request.id) ?: throw ToDoNotFoundException(request.id)

            val updatedEntity = entity.copy(content = request.newContent, dueDate = request.newDueDate)

            toDoDao.update(updatedEntity)

            updatedEntity.toDomain()
        }

        updatedEntity.await()
    }

    override fun complete(id: String): ToDo = runBlocking {
        val completedEntity = CoroutineScope(IO).async {

            val entity = toDoDao.getById(id) ?: throw ToDoNotFoundException(id)

            val completedEntity = entity.copy(completed = true)

            toDoDao.update(completedEntity)

            completedEntity.toDomain()
        }

        completedEntity.await()
    }

    override fun revert(id: String): ToDo = runBlocking {
        val revertedEntity = CoroutineScope(IO).async {

            val entity = toDoDao.getById(id) ?: throw ToDoNotFoundException(id)

            val revertedEntity = entity.copy(completed = false)

            toDoDao.update(revertedEntity)

            revertedEntity.toDomain()
        }

        revertedEntity.await()
    }

    override fun remove(id: String): ToDo = runBlocking {
        val removedEntity = CoroutineScope(IO).async {
            val entity = toDoDao.getById(id) ?: throw ToDoNotFoundException(id)

            val removedEntity = entity.copy(completed = false)

            toDoDao.delete(removedEntity)
            removedEntity.toDomain()
        }

        removedEntity.await()
    }

    override fun getById(id: String): ToDo? = runBlocking {
        val entity = CoroutineScope(IO).async {
            toDoDao.getById(id)?.toDomain()
        }

        entity.await()
    }

    override fun getAllCurrent(time: LocalDateTime): List<ToDo> = runBlocking {
        val entities = CoroutineScope(IO).async {
            toDoDao.getAllCurrent(time).map { it.toDomain() }
        }
        entities.await()
    }

    override fun getAllOverdue(time: LocalDateTime): List<ToDo> = runBlocking {
        val entities = CoroutineScope(IO).async {
            toDoDao.getAllOverdue(time).map { it.toDomain() }
        }
        entities.await()
    }

    override fun getAllCompleted(): List<ToDo> = runBlocking {
        val entities = CoroutineScope(IO).async {
            toDoDao.getAllCompleted().map { it.toDomain() }
        }
        entities.await()
    }

}

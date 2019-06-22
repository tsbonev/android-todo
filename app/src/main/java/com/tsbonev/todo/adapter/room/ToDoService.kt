package com.tsbonev.todo.adapter.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.tsbonev.todo.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoService(
    private val toDoDao: ToDoDao,
    private val idGenerator: IdGenerator
) : ToDos {
    override suspend fun add(request: AddToDoRequest): ToDo {
        val todo = CoroutineScope(IO).async {
            val entity = ToDoEntity(
                idGenerator.generateId(), request.content,
                request.dueDate, request.createdOn
            )

            toDoDao.add(entity)

            ToDo(entity)
        }

        return todo.await()
    }

    override suspend fun edit(request: EditToDoRequest): ToDo {
        val updatedEntity = CoroutineScope(IO).async {
            val entity = toDoDao.getById(request.id) ?: throw ToDoNotFoundException(request.id)

            val updatedEntity = entity.copy(content = request.newContent, dueDate = request.newDueDate)

            toDoDao.update(updatedEntity)

            ToDo(updatedEntity)
        }

        return updatedEntity.await()
    }

    override suspend fun complete(id: String): ToDo {
        val completedEntity = CoroutineScope(IO).async {

            val entity = toDoDao.getById(id) ?: throw ToDoNotFoundException(id)

            val completedEntity = entity.copy(completed = true)

            toDoDao.update(completedEntity)

            ToDo(completedEntity)
        }

        return completedEntity.await()
    }

    override suspend fun revert(id: String): ToDo {
        val revertedEntity = CoroutineScope(IO).async {

            val entity = toDoDao.getById(id) ?: throw ToDoNotFoundException(id)

            val revertedEntity = entity.copy(completed = false)

            toDoDao.update(revertedEntity)

            ToDo(revertedEntity)
        }

        return revertedEntity.await()
    }

    override suspend fun remove(id: String): ToDo {
        val removedEntity = CoroutineScope(IO).async {
            val entity = toDoDao.getById(id) ?: throw ToDoNotFoundException(id)

            val removedEntity = entity.copy(completed = false)

            toDoDao.delete(removedEntity)
            ToDo(removedEntity)
        }

        return removedEntity.await()
    }

    override suspend fun getById(id: String): ToDo? {
        val entity = CoroutineScope(IO).async {
            toDoDao.getById(id)?.let { ToDo(it) }
        }

        return entity.await()
    }

    override suspend fun getAllCurrent(time: LocalDateTime): LiveData<List<ToDo>> {
        val entities = CoroutineScope(IO).async {
            Transformations.switchMap(toDoDao.getAllCurrent(time)) {
                switchToDoLiveData(it)
            }
        }
        return entities.await()
    }

    override suspend fun getAllOverdue(time: LocalDateTime): LiveData<List<ToDo>> {
        val entities = CoroutineScope(IO).async {
            Transformations.switchMap(toDoDao.getAllOverdue(time)) {
                switchToDoLiveData(it)
            }
        }
        return entities.await()
    }

    override suspend fun getAllCompleted(): LiveData<List<ToDo>> {
        val entities = CoroutineScope(IO).async {
            Transformations.switchMap(toDoDao.getAllCompleted()) {
                switchToDoLiveData(it)
            }
        }
        return entities.await()
    }

    private fun switchToDoLiveData(input: List<ToDoEntity>): LiveData<List<ToDo>> {
        val result = MutableLiveData<List<ToDo>>()
        val toDos = input.map { entity -> ToDo(entity) }
        result.postValue(toDos)

        return result
    }
}

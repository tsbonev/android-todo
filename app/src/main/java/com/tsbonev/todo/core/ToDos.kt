package com.tsbonev.todo.core

import androidx.lifecycle.LiveData
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface ToDos {

    //region Commands
    suspend fun add(request: AddToDoRequest): ToDo

    suspend fun edit(request: EditToDoRequest): ToDo

    suspend fun complete(id: String): ToDo

    suspend fun revert(id: String): ToDo

    suspend fun remove(id: String): ToDo
    //end region

    //region Queries
    suspend fun getById(id: String): ToDo?

    suspend fun getAllCurrent(time: LocalDateTime): LiveData<List<ToDo>>

    suspend fun getAllOverdue(time: LocalDateTime): LiveData<List<ToDo>>

    suspend fun getAllCompleted(): LiveData<List<ToDo>>
    //endregion
}

data class EditToDoRequest(
    val id: String, val newContent: String,
    val newDueDate: LocalDateTime?
)

data class AddToDoRequest(
    val content: String,
    val createdOn: LocalDateTime, val dueDate: LocalDateTime?
)

data class ToDoNotFoundException(val id: String) : RuntimeException()
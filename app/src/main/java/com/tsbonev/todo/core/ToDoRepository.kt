package com.tsbonev.todo.core

import java.time.LocalDateTime
import java.util.Optional

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface ToDoRepository {

    //region Commands
    fun addToDo(request: AddToDoRequest): ToDo

    fun editToDo(request: EditToDoRequest): ToDo

    fun completeToDo(id: String): ToDo

    fun revertToDo(id: String): ToDo

    fun removeToDo(id: String): ToDo
    //end region

    //region Queries
    fun findToDosByTags(tags: Set<String>): List<ToDo>

    fun searchToDos(searchString: String): List<ToDo>

    fun getToDoById(id: String): Optional<ToDo>

    fun getExpiredToDos(date: LocalDateTime): List<ToDo>

    fun getActiveToDos(date: LocalDateTime): List<ToDo>
    //endregion
}

data class EditToDoRequest(
    val id: String, val newTitle: String, val newContent: String,
    val newTags: Set<String>, val newEndDate: LocalDateTime
)

data class AddToDoRequest(
    val title: String, val content: String,
    val createdOn: LocalDateTime, val endDate: LocalDateTime,
    val tags: Set<String>
)

data class ToDoNotFoundException(val id: String) : RuntimeException()
package com.tsbonev.todo.core

import java.time.LocalDateTime
import java.util.Optional

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface ToDoRepository {

    //region Commands
    fun add(request: AddToDoRequest): ToDo

    fun edit(request: EditToDoRequest): ToDo

    fun complete(id: String): ToDo

    fun revert(id: String): ToDo

    fun remove(id: String): ToDo
    //end region

    //region Queries
    fun searchByContent(searchString: String, sortOrder: SortOrder, cursor: Cursor): List<ToDo>

    fun getById(id: String): ToDo?

    fun getAllCurrent(cursor: Cursor, time: LocalDateTime): List<ToDo>

    fun getAllOverdue(cursor: Cursor, time: LocalDateTime): List<ToDo>

    fun getAllCompleted(cursor: Cursor): List<ToDo>
    //endregion
}

data class Cursor(val offset: Int = 0, val limit: Int = 1)

enum class SortOrder {
    ASCENDING_DUE_DATE, DESCENDING_DUE_DATE
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
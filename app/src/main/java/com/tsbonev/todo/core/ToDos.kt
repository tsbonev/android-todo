package com.tsbonev.todo.core

import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
interface ToDos {

    //region Commands
    fun add(request: AddToDoRequest): ToDo

    fun edit(request: EditToDoRequest): ToDo

    fun complete(id: String): ToDo

    fun revert(id: String): ToDo

    fun remove(id: String): ToDo
    //end region

    //region Queries
    fun getById(id: String): ToDo?

    fun getAllCurrent(time: LocalDateTime): List<ToDo>

    fun getAllOverdue(time: LocalDateTime): List<ToDo>

    fun getAllCompleted(): List<ToDo>
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
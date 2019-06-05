package com.tsbonev.todo.core

import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDo(val id: String,
                val content: String,
                val dueDate: LocalDateTime?,
                val createdOn: LocalDateTime,
                val status: ToDoStatus)

enum class ToDoStatus {
    COMPLETED, CURRENT
}
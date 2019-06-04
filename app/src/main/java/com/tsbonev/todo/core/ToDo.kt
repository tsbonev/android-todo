package com.tsbonev.todo.core

import java.time.LocalDateTime

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
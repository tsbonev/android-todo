package com.tsbonev.todo.core.model

import com.tsbonev.todo.core.ToDo

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDoViewModel(val id: String,
                    val content: String,
                    val dueDate: String?,
                    val createdOn: String,
                    val status: String) {
    constructor(todo: ToDo) : this(
        todo.id,
        todo.content,
        todo.dueDate?.toString(),
        todo.createdOn.toString(),
        todo.status.name
    )
}
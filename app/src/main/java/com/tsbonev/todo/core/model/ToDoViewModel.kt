package com.tsbonev.todo.core.model

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDoViewModel(val id: String,
                    val content: String,
                    val dueDate: String?,
                    val createdOn: String,
                    val status: String)
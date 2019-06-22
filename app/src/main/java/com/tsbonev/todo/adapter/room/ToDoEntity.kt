package com.tsbonev.todo.adapter.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
@Entity(tableName = "todos")
data class ToDoEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val dueDate: LocalDateTime?,
    val createdOn: LocalDateTime,
    val completed: Boolean = false
)
package com.tsbonev.todo.adapter.room

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.tsbonev.todo.core.ToDo
import com.tsbonev.todo.core.ToDoStatus
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
@Entity(tableName = "todos")
data class ToDoEntity (
    @PrimaryKey
    val id: String,
    val content: String,
    val dueDate: LocalDateTime?,
    val createdOn: LocalDateTime,
    val completed: Boolean = false
) {
    @Ignore
    fun toDomain() : ToDo {
        return ToDo(id, content, dueDate, createdOn, if(completed) ToDoStatus.COMPLETED else ToDoStatus.CURRENT)
    }
}
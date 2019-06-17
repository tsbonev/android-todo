package com.tsbonev.todo.adapter.room

import androidx.room.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
@Dao
interface ToDoDao {
    @Insert
    fun add(todo: ToDoEntity)

    @Update
    fun update(todo: ToDoEntity)

    @Delete
    fun delete(todo: ToDoEntity)

    @Query("SELECT * FROM todos WHERE id LIKE :id")
    fun getById(id: String): ToDoEntity?

    @Query("SELECT * FROM todos WHERE (dueDate > :time OR dueDate IS NULL) AND completed = 0")
    fun getAllCurrent(time: LocalDateTime): List<ToDoEntity>

    @Query("SELECT * FROM todos WHERE dueDate <= :time AND completed = 0")
    fun getAllOverdue(time: LocalDateTime): List<ToDoEntity>

    @Query("SELECT * FROM todos WHERE completed = 1")
    fun getAllCompleted(): List<ToDoEntity>
}

@Database(entities = arrayOf(ToDoEntity::class), version = 1)
@TypeConverters(LocalDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toDoDao(): ToDoDao
}

object LocalDateTimeConverters {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let {
            formatter.parse(value, LocalDateTime::from)
        }
    }

    @TypeConverter
    @JvmStatic
    fun fromLocalDateTime(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}
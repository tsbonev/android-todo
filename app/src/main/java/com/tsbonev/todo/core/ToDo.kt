package com.tsbonev.todo.core

import java.time.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDo(val id: String,
                val endDate: LocalDateTime,
                val title: String,
                val content: String,
                val tags : Set<String>,
                val completed: Boolean = false)
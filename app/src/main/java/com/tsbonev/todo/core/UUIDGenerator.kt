package com.tsbonev.todo.core

import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class UUIDGenerator : IdGenerator {
    override fun generateId(): String {
        return UUID.randomUUID().toString()
    }
}
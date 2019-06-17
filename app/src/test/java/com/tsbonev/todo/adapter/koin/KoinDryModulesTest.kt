package com.tsbonev.todo.adapter.koin

import org.junit.After
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class KoinDryModulesTest : KoinTest {
    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun `Checks Koin modules`() {
        startKoin { listOf(roomInMemoryModule, toDoModule) }
    }
}
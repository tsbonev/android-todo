package com.tsbonev.todo.helper

import org.jmock.Expectations
import org.jmock.Mockery

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */

fun Mockery.expecting(block: Expectations.() -> Unit) {
    checking(Expectations().apply(block))
}
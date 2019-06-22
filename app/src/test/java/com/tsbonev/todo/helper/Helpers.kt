package com.tsbonev.todo.helper

import androidx.lifecycle.*
import org.jmock.Expectations
import org.jmock.Mockery

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */

fun Mockery.expecting(block: Expectations.() -> Unit) {
    checking(Expectations().apply(block))
}

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}

class OneTimeObserver<T>(private val handler: (T) -> Unit) : Observer<T>, LifecycleOwner {
    private val lifecycle = LifecycleRegistry(this)

    init {
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun getLifecycle(): Lifecycle = lifecycle

    override fun onChanged(t: T) {
        handler(t)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }
}
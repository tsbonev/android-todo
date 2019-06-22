package com.tsbonev.todo

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tsbonev.todo.adapter.koin.roomInMemoryModule
import com.tsbonev.todo.adapter.koin.roomPersistentModule
import com.tsbonev.todo.adapter.koin.toDoModule
import com.tsbonev.todo.adapter.koin.toDoViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(listOf(
                roomInMemoryModule,
                toDoModule,
                toDoViewModelModule
            ))
        }
    }
}
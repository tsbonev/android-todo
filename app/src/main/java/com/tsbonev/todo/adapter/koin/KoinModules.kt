package com.tsbonev.todo.adapter.koin

import androidx.room.Room
import com.tsbonev.todo.adapter.room.AppDatabase
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.IdGenerator
import com.tsbonev.todo.core.UUIDGenerator
import com.tsbonev.todo.core.model.ToDoViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */

val roomInMemoryModule = module {
    single { Room.inMemoryDatabaseBuilder(androidContext(), AppDatabase::class.java).build() }
}

val roomPersistentModule = module {
    single { Room.databaseBuilder(androidContext(), AppDatabase::class.java, "ToDoDb").build() }
}

val toDoModule = module {
    data class DbHolder(val db: AppDatabase)

    single<IdGenerator> { UUIDGenerator() }

    single { DbHolder(db = get()).db.toDoDao() }

    single { ToDoService(toDoDao = get(), idGenerator = get()) }
}

val toDoViewModelModule = module {
    viewModel { ToDoViewModel(toDoService = get()) }
}
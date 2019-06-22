package com.tsbonev.todo.core.model

import androidx.lifecycle.*
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.ToDo
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDoViewModel(val toDoService: ToDoService) : ViewModel() {
    private var currentToDos: MutableLiveData<List<ToDo>> = MutableLiveData()
    private var overdueToDos: MutableLiveData<List<ToDo>> = MutableLiveData()
    private var completedToDos: MutableLiveData<List<ToDo>> = MutableLiveData()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val time = LocalDateTime.now()

            toDoService.getAllCurrent(time).observeForever {
                currentToDos.postValue(it)
            }

            toDoService.getAllOverdue(time).observeForever {
                overdueToDos.postValue(it)
            }

            toDoService.getAllCompleted().observeForever {
                completedToDos.postValue(it)
            }
        }
    }

    fun current(): LiveData<List<ToDo>> {
        return currentToDos
    }

    fun overdue(): LiveData<List<ToDo>> {
        return overdueToDos
    }

    fun completed(): LiveData<List<ToDo>> {
        return completedToDos
    }
}
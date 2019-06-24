package com.tsbonev.todo.core.model

import androidx.lifecycle.*
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.EditToDoRequest
import com.tsbonev.todo.core.ToDo
import com.tsbonev.todo.core.ui.fragment.ToDoType
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
data class ToDoViewModel(private val toDoService: ToDoService) : ViewModel() {
    private var currentToDos: MutableLiveData<List<ToDo>> = MutableLiveData()
    private var overdueToDos: MutableLiveData<List<ToDo>> = MutableLiveData()
    private var completedToDos: MutableLiveData<List<ToDo>> = MutableLiveData()

    private val currentToDosObserver = Observer<List<ToDo>> {
        currentToDos.postValue(it)
    }

    private val overdueToDosObserver = Observer<List<ToDo>> {
        overdueToDos.postValue(it)
    }

    private val completedToDosObserver = Observer<List<ToDo>> {
        completedToDos.postValue(it)
    }

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val time = LocalDateTime.now()

            toDoService.getAllCurrent(time).observeForever(currentToDosObserver)

            toDoService.getAllOverdue(time).observeForever(overdueToDosObserver)

            toDoService.getAllCompleted().observeForever(completedToDosObserver)
        }
    }

    fun complete(id: String) {
        viewModelScope.launch {
            toDoService.complete(id)
        }
    }

    fun revert(id: String) {
        viewModelScope.launch {
            toDoService.revert(id)
        }
    }

    fun remove(id: String) {
        viewModelScope.launch {
            toDoService.remove(id)
        }
    }

    fun add(content: String, dueDate: LocalDateTime?) {
        viewModelScope.launch {
            toDoService.add(AddToDoRequest(content, LocalDateTime.now(), dueDate))
        }
    }

    fun edit(id: String, content: String, dueDate: LocalDateTime?) {
        viewModelScope.launch {
            toDoService.edit(EditToDoRequest(id, content, dueDate))
        }
    }

    fun toDosOfType(type: ToDoType): LiveData<List<ToDo>> {
        return when(type) {
            ToDoType.CURRENT -> currentToDos
            ToDoType.OVERDUE -> overdueToDos
            ToDoType.COMPLETED -> completedToDos
        }
    }

    override fun onCleared() {
        super.onCleared()
        currentToDos.removeObserver(currentToDosObserver)
        completedToDos.removeObserver(completedToDosObserver)
        overdueToDos.removeObserver(overdueToDosObserver)
    }
}
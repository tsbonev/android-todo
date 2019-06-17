package com.tsbonev.todo.core.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tsbonev.todo.R
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.ui.ToDoRecyclerAdapter
import com.tsbonev.todo.core.model.ToDoViewModel
import org.koin.android.ext.android.inject
import org.threeten.bp.LocalDateTime
import com.tsbonev.todo.databinding.TodoFragmentBinding

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoFragment(private val type: ToDoType) : Fragment() {

    companion object {
        fun newInstance(type: ToDoType) = ToDoFragment(type)
    }

    private val toDoService: ToDoService by inject()
    private lateinit var adapter: ToDoRecyclerAdapter
    private lateinit var binding: TodoFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.todo_fragment, container, false)
        val toDos = when(type) {
            ToDoType.CURRENT -> toDoService.getAllCurrent(LocalDateTime.now()).map { ToDoViewModel(it) }

            ToDoType.COMPLETED -> toDoService.getAllCompleted().map { ToDoViewModel(it) }

            ToDoType.OVERDUE -> toDoService.getAllOverdue(LocalDateTime.now()).map { ToDoViewModel(it) }
        }

        adapter = ToDoRecyclerAdapter(toDos)

        binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.adapter = adapter

        return binding.root
    }
}

enum class ToDoType {
    CURRENT, OVERDUE, COMPLETED
}
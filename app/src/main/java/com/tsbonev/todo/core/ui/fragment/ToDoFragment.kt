package com.tsbonev.todo.core.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsbonev.todo.R
import com.tsbonev.todo.core.ToDo
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.core.ui.ToDoRecyclerAdapter
import com.tsbonev.todo.databinding.TodoFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoFragment(private val type: ToDoType) : Fragment() {

    companion object {
        fun newInstance(type: ToDoType) = ToDoFragment(type)
    }

    private lateinit var adapter: ToDoRecyclerAdapter
    private lateinit var binding: TodoFragmentBinding
    private val toDoViewModel: ToDoViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.todo_fragment, container, false)
        adapter = ToDoRecyclerAdapter(toDoViewModel, listOf())

        toDoViewModel.toDosOfType(type).observe(this,
            Observer<List<ToDo>> { t -> adapter.setToDos(t) }
        )

        binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity!!)
        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.adapter = adapter

        return binding.root
    }
}

enum class ToDoType {
    CURRENT, OVERDUE, COMPLETED
}
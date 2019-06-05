package com.tsbonev.todo.core.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tsbonev.todo.R
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.databinding.TodoItemLayoutBinding

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoRecyclerAdapter(private val context: Context, private val toDoList: List<ToDoViewModel>) : RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ToDoViewHolder {
        val todoLayoutBinding = DataBindingUtil.inflate<TodoItemLayoutBinding>(LayoutInflater.from(viewGroup.context),
            R.layout.todo_item_layout, viewGroup, false)

        return ToDoViewHolder(todoLayoutBinding)
    }

    override fun getItemCount(): Int {
        return toDoList.count()
    }

    override fun onBindViewHolder(viewHolder: ToDoViewHolder, position: Int) {
        val todo = toDoList[position]

        viewHolder.binding.toDo = todo
    }

    class ToDoViewHolder(val binding: TodoItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}
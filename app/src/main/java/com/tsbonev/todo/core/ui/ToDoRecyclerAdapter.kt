package com.tsbonev.todo.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tsbonev.todo.R
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.databinding.TodoItemLayoutBinding

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoRecyclerAdapter(private val toDoList: List<ToDoViewModel>) :
    RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ToDoViewHolder {
        val todoLayoutBinding = DataBindingUtil.inflate<TodoItemLayoutBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.todo_item_layout, viewGroup, false
        )

        return ToDoViewHolder(todoLayoutBinding)
    }

    override fun getItemCount(): Int {
        return toDoList.count()
    }

    override fun onBindViewHolder(viewHolder: ToDoViewHolder, position: Int) {
        val todo = toDoList[position]

        viewHolder.binding.toDo = todo
    }

    class ToDoViewHolder(val binding: TodoItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.todoWrapper.setOnClickListener {
                Toast.makeText(binding.root.context, binding.toDo!!.content, Toast.LENGTH_SHORT).show()
            }

            binding.todoWrapper.setOnLongClickListener {
                Toast.makeText(binding.root.context, binding.toDo!!.id, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}
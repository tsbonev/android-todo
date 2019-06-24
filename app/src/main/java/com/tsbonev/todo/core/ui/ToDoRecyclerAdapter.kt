package com.tsbonev.todo.core.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tsbonev.todo.R
import com.tsbonev.todo.core.ToDo
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.core.ui.fragment.ToDoType
import com.tsbonev.todo.databinding.TodoItemLayoutBinding

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoRecyclerAdapter(private val viewModel: ToDoViewModel, private var toDos: List<ToDo>) :
    RecyclerView.Adapter<ToDoRecyclerAdapter.ToDoViewHolder>() {

    fun setToDos(list: List<ToDo>) {
        toDos = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): ToDoViewHolder {
        val todoLayoutBinding = DataBindingUtil.inflate<TodoItemLayoutBinding>(
            LayoutInflater.from(viewGroup.context),
            R.layout.todo_item_layout, viewGroup, false
        )
        return ToDoViewHolder(todoLayoutBinding, viewModel)
    }

    override fun getItemCount(): Int {
        return toDos.size
    }

    override fun onBindViewHolder(viewHolder: ToDoViewHolder, position: Int) {
        val todo = toDos[position]

        viewHolder.binding.toDo = todo
    }

    class ToDoViewHolder(val binding: TodoItemLayoutBinding, private val viewModel: ToDoViewModel) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.todoWrapper.setOnClickListener {
                Toast.makeText(binding.root.context, binding.toDo!!.completed.toString(), Toast.LENGTH_SHORT).show()
            }

            binding.completedBtn.setOnClickListener {
                if(binding.toDo!!.completed.not()){
                    viewModel.complete(binding.toDo!!.id)
                    Toast.makeText(binding.root.context, "Completed", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.revert(binding.toDo!!.id)
                    Toast.makeText(binding.root.context, "Reverted", Toast.LENGTH_SHORT).show()
                }
            }

            binding.todoWrapper.setOnLongClickListener {
                viewModel.remove(binding.toDo!!.id)
                Toast.makeText(binding.root.context, "Deleted", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
}
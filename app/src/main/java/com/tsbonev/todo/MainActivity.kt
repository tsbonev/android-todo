package com.tsbonev.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.adapter.ToDoRecyclerAdapter
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.databinding.ActivityMainBinding
import org.koin.android.ext.android.inject
import org.threeten.bp.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: ToDoRecyclerAdapter

    private val toDoService: ToDoService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val currentToDos = toDoService.getAllCurrent(LocalDateTime.now())

        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainBinding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.mainRecyclerView.setHasFixedSize(true)
        adapter = ToDoRecyclerAdapter(this, currentToDos.map {
            ToDoViewModel(it)
        }
        )

        mainBinding.mainRecyclerView.adapter = adapter
    }
}

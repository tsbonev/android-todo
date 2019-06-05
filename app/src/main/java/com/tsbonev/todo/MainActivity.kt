package com.tsbonev.todo

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.tsbonev.todo.core.adapter.ToDoRecyclerAdapter
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: ToDoRecyclerAdapter

    private val toDoLists = listOf(ToDoViewModel("::id::", "Do laundry.", "24h", "::createdOn::", "::status::"),
        ToDoViewModel("::id::", "Walk dog.", "1h", "::createdOn::", "::status::"),
        ToDoViewModel("::id::", "Do dishes.", "2h 30m", "::createdOn::", "::status::"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainBinding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.mainRecyclerView.setHasFixedSize(true)
        adapter = ToDoRecyclerAdapter(this, toDoLists)

        mainBinding.mainRecyclerView.adapter = adapter
    }
}

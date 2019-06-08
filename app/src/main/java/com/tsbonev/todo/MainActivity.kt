package com.tsbonev.todo

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.jakewharton.threetenabp.AndroidThreeTen
import com.tsbonev.todo.adapter.room.AppDatabase
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.EditToDoRequest
import com.tsbonev.todo.core.UUIDGenerator
import com.tsbonev.todo.core.adapter.ToDoRecyclerAdapter
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.databinding.ActivityMainBinding
import org.threeten.bp.LocalDateTime

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: ToDoRecyclerAdapter

    private val toDoLists = listOf(ToDoViewModel("::id::", "Do laundry.", "24h", "::createdOn::", "::status::"),
        ToDoViewModel("::id::", "Walk dog.", "1h", "::createdOn::", "::status::"),
        ToDoViewModel("::id::", "Do dishes.", "2h 30m", "::createdOn::", "::status::"))

    override fun onCreate(savedInstanceState: Bundle?) {
        val database = Room.inMemoryDatabaseBuilder(this, AppDatabase::class.java).build()

        val idGenerator = UUIDGenerator()

        val service = ToDoService(database.toDoDao(), idGenerator)

        val addedToDo = service.add(AddToDoRequest("::content::", LocalDateTime.now(), LocalDateTime.now()))

        service.edit(EditToDoRequest(addedToDo.id, "::newContent::", LocalDateTime.now().plusDays(2)))

        val retrievedToDo = service.getById(addedToDo.id)

        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mainBinding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        mainBinding.mainRecyclerView.setHasFixedSize(true)
        adapter = ToDoRecyclerAdapter(this, listOf(
          ToDoViewModel(addedToDo), ToDoViewModel(retrievedToDo!!)
        ))

        mainBinding.mainRecyclerView.adapter = adapter
    }
}

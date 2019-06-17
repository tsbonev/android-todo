package com.tsbonev.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.ui.ToDoRecyclerAdapter
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.core.ui.ToDoViewPageAdapter
import com.tsbonev.todo.databinding.ActivityMainBinding
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service: ToDoService by inject()

        service.add(AddToDoRequest("::current::", LocalDateTime.now(), null))
        val todo = service.add(AddToDoRequest("::completed::", LocalDateTime.now(), null))
        service.add(AddToDoRequest("::overdue::", LocalDateTime.now(), LocalDateTime.now().minusDays(1)))


        service.complete(todo.id)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewPager.adapter = ToDoViewPageAdapter(supportFragmentManager)
    }
}

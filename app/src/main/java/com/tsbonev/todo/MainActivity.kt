package com.tsbonev.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.tsbonev.todo.adapter.room.ToDoService
import com.tsbonev.todo.core.AddToDoRequest
import com.tsbonev.todo.core.ui.ToDoViewPageAdapter
import com.tsbonev.todo.databinding.ActivityMainBinding
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.threeten.bp.LocalDateTime

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val service: ToDoService by inject()

        runBlocking {
            service.add(AddToDoRequest("::current::", LocalDateTime.now(), null))
            service.add(AddToDoRequest("::currentDue1::", LocalDateTime.now(), LocalDateTime.now().plusMinutes(12)))
            service.add(AddToDoRequest("::currentDue2::", LocalDateTime.now(), LocalDateTime.now().plusDays(2)))
            service.add(AddToDoRequest("::currentDue3::", LocalDateTime.now(), LocalDateTime.now().plusMonths(2).plusDays(1)))
            val todo = service.add(AddToDoRequest("::completed::", LocalDateTime.now(), null))
            service.add(AddToDoRequest("::overdue::", LocalDateTime.now(), LocalDateTime.now().minusDays(1)))

            service.complete(todo.id)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewPager.adapter = ToDoViewPageAdapter(supportFragmentManager)
        binding.viewPager.currentItem = 1
        binding.viewPager.offscreenPageLimit = 2
    }
}

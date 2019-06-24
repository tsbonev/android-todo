package com.tsbonev.todo.core.ui.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tsbonev.todo.R
import com.tsbonev.todo.core.model.ToDoViewModel
import com.tsbonev.todo.databinding.TodoAddFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import java.util.*

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class AddToDoFragment : DialogFragment() {

    companion object {
        fun newInstance() = AddToDoFragment()
    }
    private val toDoViewModel: ToDoViewModel by viewModel()
    private lateinit var binding: TodoAddFragmentBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.todo_add_fragment, container, false)

        binding.toDoAdd = ToDoAddRequest()

        binding.datePicker.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(this.context!!, DatePickerDialog.OnDateSetListener { _, yearPicked, monthPicked, dayPicked ->

                binding.toDoAdd!!.date = LocalDate.of(yearPicked, monthPicked + 1, dayPicked).toString()
                binding.datePicked.text = LocalDate.of(yearPicked, monthPicked + 1 , dayPicked).toString()

            }, year, month, day)

            dpd.show()
        }

        binding.timePicker.setOnClickListener {
            val c = Calendar.getInstance()
            val hours = c.get(Calendar.HOUR_OF_DAY)
            val minutes = c.get(Calendar.MINUTE)

            val tpd = TimePickerDialog(this.context!!, TimePickerDialog.OnTimeSetListener { _, hourPicked, minutePicked ->

                binding.toDoAdd!!.time = LocalTime.of(hourPicked, minutePicked).toString()
                binding.timePicked.text = LocalTime.of(hourPicked, minutePicked).toString()

            }, hours, minutes, true)

            tpd.show()
        }

        binding.createTodoBtn.setOnClickListener {
            val dueDate = if(binding.toDoAdd!!.time.isEmpty() || binding.toDoAdd!!.date.isEmpty()) null else {
                val localDate = LocalDate.parse(binding.toDoAdd!!.date)
                val localTime = LocalTime.parse(binding.toDoAdd!!.time)
                LocalDateTime.of(localDate, localTime)
            }

            Toast.makeText(context!!, dueDate.toString(), Toast.LENGTH_SHORT).show()

            toDoViewModel.add(binding.content.text.toString(), dueDate)
            dialog.dismiss()
        }



        binding.createDueDateLessTodoBtn.setOnClickListener {
            toDoViewModel.add(binding.content.text.toString(), null)
            dialog.dismiss()
        }

        return binding.root
    }
}

data class ToDoAddRequest(
    var content: String = "", var time: String = "",
    var date: String = ""
)

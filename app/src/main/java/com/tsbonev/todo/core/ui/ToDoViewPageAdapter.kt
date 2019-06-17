package com.tsbonev.todo.core.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.tsbonev.todo.core.ui.fragment.ToDoFragment
import com.tsbonev.todo.core.ui.fragment.ToDoType

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class ToDoViewPageAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {
    private val PAGE_NUM = 3

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ToDoFragment.newInstance(ToDoType.CURRENT)
            1 -> ToDoFragment.newInstance(ToDoType.COMPLETED)
            2 -> ToDoFragment.newInstance(ToDoType.OVERDUE)
            else -> error("No such page")
        }
    }

    override fun getCount(): Int {
        return PAGE_NUM
    }
}
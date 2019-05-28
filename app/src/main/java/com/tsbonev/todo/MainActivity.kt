package com.tsbonev.todo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tsbonev.todo.ui.MainActivityUI
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUI().setContentView(this)
    }
}

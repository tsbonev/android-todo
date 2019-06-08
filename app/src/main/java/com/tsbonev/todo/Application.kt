package com.tsbonev.todo

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}
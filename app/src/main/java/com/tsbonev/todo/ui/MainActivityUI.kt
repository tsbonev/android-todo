package com.tsbonev.todo.ui

import android.content.res.ColorStateList
import android.os.Build
import android.view.Gravity
import android.view.View
import com.tsbonev.todo.MainActivity
import com.tsbonev.todo.R
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import kotlin.random.Random

/**
 * @author Tsvetozar Bonev (tsbonev@gmail.com)
 */
class MainActivityUI : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        frameLayout {
            val textField = editText {
                hint = "Text for toast and snackbars"
            }.lparams(width = matchParent) {
                margin = dip(15)
                topMargin = dip(30)
            }

            imageView(R.drawable.abc_btn_check_to_on_mtrl_000) {
                onClick {
                    val randInt = Random.nextInt()
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                        this@imageView.imageTintList =
                                ColorStateList.valueOf(randInt)
                }
            }.lparams(dip(72), dip(72)) {
                gravity = Gravity.CENTER
            }

            linearLayout {
                button("Show toast") {
                    onClick {
                        toast(textField.text)
                    }
                }
            }.lparams {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = dip(72)
            }
        }
    }
}
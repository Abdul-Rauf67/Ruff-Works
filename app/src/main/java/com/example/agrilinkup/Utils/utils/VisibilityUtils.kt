package com.example.agrilinkup.Utils.utils

import android.view.View

object VisibilityUtils {
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.INVISIBLE
    }

    fun View.gone() {
        visibility = View.GONE
    }

}

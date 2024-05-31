@file:Suppress("DEPRECATION")
package com.example.agrilinkup.utils

import android.app.Activity
import android.content.res.Configuration
import android.view.View

object StatusBarUtils {
    fun setStatusBarDarkMode(activity: Activity) {
        val decorView = activity.window.decorView
        val flags = decorView.systemUiVisibility
        val lightStatusBarFlag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (isLightMode(activity)) {
            decorView.systemUiVisibility = flags or lightStatusBarFlag
        } else {
            decorView.systemUiVisibility = flags and lightStatusBarFlag.inv()
        }
    }

    private fun isLightMode(activity: Activity): Boolean {
        val mode = activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return mode == Configuration.UI_MODE_NIGHT_NO
    }

}
package com.example.agrilinkup.Utils.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.MediaController

class CustomMediaController(context: Context) : MediaController(context) {
    override fun setAnchorView(view: View?) {
        super.setAnchorView(view)
        val params = view?.layoutParams as ViewGroup.LayoutParams
        if (params is MarginLayoutParams) {
            params.bottomMargin = 30
            view.layoutParams = params
        }
    }
}
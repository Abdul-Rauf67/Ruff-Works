package com.example.agrilinkup

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.graphics.Path
import android.graphics.RectF
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView @JvmOverloads constructor(
    context: Context,
    attrs:AttributeSet?=null,
    defStyleAttr:Int=0
): AppCompatImageView(context, attrs,defStyleAttr) {
    private val clipPath=Path()

    override fun onDraw(canvas: Canvas) {
        val rect=RectF(0f,0f,width.toFloat(),height.toFloat())
        clipPath.addRoundRect(rect,width.toFloat()/2,
            height.toFloat()/2,Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.onDraw(canvas)
    }

}
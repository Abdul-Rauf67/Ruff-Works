package com.example.agrilinkup.utils
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt

object Spanny {
    fun spannableText(textView: TextView, fullText: String, clickableText: String,clickableTextColor: Int = Color.BLUE,onClickAction: () -> Unit, ) {
        val spannableString = SpannableString(fullText)

        val startIndex = fullText.indexOf(clickableText)
        if (startIndex != -1) {
            val endIndex = startIndex + clickableText.length

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(view: View) {
                    onClickAction.invoke()
                }

                override fun updateDrawState(ds: TextPaint) {
                    // Remove underline and color change
                    ds.isUnderlineText = false
                    ds.color = clickableTextColor
                }
            }

            // Set the clickable text's style
            spannableString.setSpan(clickableSpan, startIndex, endIndex, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(
                StyleSpan(Typeface.NORMAL),
                startIndex,
                endIndex,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

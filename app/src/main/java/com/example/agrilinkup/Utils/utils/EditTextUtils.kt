package com.example.agrilinkup.utils

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.agrilinkup.R

@SuppressLint("ClickableViewAccessibility")
fun setPasswordVisibilityToggle(editText: EditText, ) { editText.setOnTouchListener { view, motionEvent ->
    val drawableEnd = editText.compoundDrawablesRelative[2]
    if (drawableEnd != null && motionEvent.action == MotionEvent.ACTION_UP) {
        val bounds = drawableEnd.bounds
        val x = motionEvent.x.toInt()

        if (x >= view.width - view.paddingRight - bounds.width()) {
            val cursorPosition = editText.selectionStart
            val isPasswordVisible =
                editText.transformationMethod == PasswordTransformationMethod.getInstance()

            editText.transformationMethod =
                if (isPasswordVisible) HideReturnsTransformationMethod.getInstance()
                else PasswordTransformationMethod.getInstance()

            editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                0,
                0,
                if (isPasswordVisible) R.drawable.icon_password_visibility_off else R.drawable.icon_password_visibility_on,
                0
            )

            editText.setSelection(cursorPosition)
            return@setOnTouchListener true
        }
    }
    false
} }

@SuppressLint("UseCompatLoadingForDrawables")
fun Fragment.setEditTextFocusChangeBackground(editText1: EditText,editText2: EditText? = null) {
    editText1.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            editText1.background = requireContext().getDrawable(R.drawable.bg_editext_with_stroke)
        } else {
            editText1.background = requireContext().getDrawable(R.drawable.bg_edittext)
        }
    }

    editText2?.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus){
            editText2.background = requireContext().getDrawable(R.drawable.bg_editext_with_stroke)
        } else{
            editText2.background = requireContext().getDrawable(R.drawable.bg_edittext)
        }
    }
}

fun editTextWatcherForChatButton(editText: EditText, sendBtn: View, voiceBtn: View){
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable?) {
            val text = s?.toString() ?: ""

            if (text.isNotBlank()){
                sendBtn.visible()
                voiceBtn.invisible()
            }
            else {
                sendBtn.invisible()
                voiceBtn.visible()
            }
        }
    })
}

fun EditText.ifEmpty(errorMessage: String): Boolean {
    val text = this.text?.toString()
    if (text.isNullOrEmpty()) {
        this.error = errorMessage
        this.requestFocus()
        return true
    }
    return false
}

fun EditText.ifEmailNotMatches(errorMessage: String):Boolean{
    val email = this.text?.toString()
    if (!email?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
        this.error = errorMessage
        this.requestFocus()
        return true
    }
    return false
}



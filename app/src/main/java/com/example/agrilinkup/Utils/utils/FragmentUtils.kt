package com.example.agrilinkup.Utils.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.util.Base64
import com.example.agrilinkup.utils.DateTimeUtils


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.btnEnableDisableTextWatcherForButton(
    editText1: EditText,
    button: Button,
    editText2: EditText? = null
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val text1 = editText1.text.toString()
            val text2 = editText2?.text.toString()

            if (text1.isNotEmpty() && (editText2 == null || text2.isNotEmpty())) {
                button.alpha = 1.0f
                button.isEnabled = true
            } else {
                button.alpha = 0.6f
                button.isEnabled = false
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }
    editText1.addTextChangedListener(textWatcher)
    editText2?.addTextChangedListener(textWatcher)
}

fun Fragment.handleSendButtonAndMicrophoneSwitchingOnTextChange(
    editText: EditText,
    sent: View,
    microphone: View
) {
    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val text = editText.text.toString()

            if (text.isNotEmpty()) {
                microphone.gone()
                sent.visible()
            } else {
                sent.gone()
                microphone.visible()
            }
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }
    editText.addTextChangedListener(textWatcher)
}
fun openAppSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    activity.startActivity(intent)
}

fun TextView.setTimestamp(timestamp: Long) {
    text = DateTimeUtils.formatDateTime(timestamp)
}

fun Context.getHashKeyForFB() {
    try {
        val info: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            val hashKey: String = Base64.encodeToString(md.digest(), Base64.DEFAULT)
            Log.d("hashKey", "Hash Key: $hashKey")
        }
    } catch (e: NoSuchAlgorithmException) {
        Log.e("hashKey", "printHashKey()", e)
    } catch (e: Exception) {
        Log.e("hashKey", "printHashKey()", e)
    }
}





package com.example.agrilinkup.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun TextView.setTimestamp(timestamp: Long) {
    text = DateTimeUtils.formatHoursMinutes(timestamp)
}

fun Activity.startActivity(destinationActivity: Class<*>) {
    val intent = Intent(this, destinationActivity)
    startActivity(intent)
}

fun Fragment.startActivity(destinationActivity: Class<*>) {
    val intent = Intent(requireActivity(), destinationActivity)
    startActivity(intent)
}

fun Fragment.toast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun Activity.getColorFromId(colorId: Int): Int {
    return this.resources.getColor(colorId, null)
}

fun Fragment.getColorFromId(colorId: Int): Int {
    return requireContext().resources.getColor(colorId, null)
}

fun Context.getColorFromId(colorId: Int): Int {
    return this.resources.getColor(colorId, null)
}

fun openAppSettings(activity: Activity) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", activity.packageName, null)
    intent.data = uri
    activity.startActivity(intent)
}


inline fun <reified T : ViewDataBinding> AppCompatActivity.dataBinding(
    crossinline bindingInflater: (LayoutInflater) -> T): Lazy<T> =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater).also {
            setContentView(it.root)
        }
    }

inline fun <reified T : ViewDataBinding> Fragment.dataBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
}




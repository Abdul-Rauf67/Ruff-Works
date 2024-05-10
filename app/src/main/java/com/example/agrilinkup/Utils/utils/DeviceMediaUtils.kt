package com.example.agrilinkup.utils

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

fun extractSelectedUris(data: Intent?): ArrayList<Uri> {
    val selectedMediaUris = ArrayList<Uri>()
    when {
        data != null -> {
            if (data.clipData != null) {
                val clipData = data.clipData
                if (clipData != null) {
                    for (i in 0 until clipData.itemCount) {
                        val uri = clipData.getItemAt(i).uri
                        selectedMediaUris.add(uri)
                    }
                }
            } else if (data.data != null) {
                val uri = data.data
                if (uri != null) {
                    selectedMediaUris.add(uri)
                }
            }
        }
    }
    return selectedMediaUris
}

fun Fragment.filterUrisByMimeType(uris: ArrayList<Uri>, mimeTypePrefix: String): List<Uri> {
    return uris.filter {
        getMimeType(it).startsWith(mimeTypePrefix)
    }
}

fun Fragment.getMimeType(uri: Uri): String {
    val contentResolver = requireContext().contentResolver
    return contentResolver.getType(uri) ?: ""
}

fun pickMediaIntent(): Intent {
    return Intent(Intent.ACTION_PICK).apply {
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        type = "video/* image/*"
    }
}



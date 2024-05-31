package com.example.agrilinkup.utils
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import android.widget.Toast

object ClipBoardUtils {
    fun copyTextToClipboard(context: Context, view: TextView) {
        val textToCopy = view.text.toString()

        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }
}
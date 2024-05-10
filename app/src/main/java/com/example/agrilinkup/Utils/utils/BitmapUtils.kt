package com.example.agrilinkup.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {
    fun getBitmapFromView(view: View): Bitmap {
        var bitmap: Bitmap? = null
        try {
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap!!
    }

    fun saveBitmapToGallery(bitmap: Bitmap, context: Context) {
        val fileName = "image_${DateTimeUtils.formatCompleteDateAndTime()}.png"

        /*........ MetaData for media file ........*/
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/HsEreceipts")
        }

        /*........ Content resolver to interact with content provider ........*/
        val resolver = context.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val imgUri = resolver.insert(imageCollection, contentValues)
        if (imgUri != null) {
            resolver.openOutputStream(imgUri).use { outPutStream ->
                if (outPutStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
                    Toast.makeText(context, "E-Receipt Saved To Gallery", Toast.LENGTH_SHORT).show()
                }
            }
            /*........ Notify the system that an media item is inserted ........*/
            MediaScannerConnection.scanFile(context, arrayOf(imgUri.path), arrayOf("image/png")){ _, _ ->}
        }
        else{
            Toast.makeText(context, "Error: Image not saved", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareBitmap(context: Context, bitmap: Bitmap) {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()

        /*........ compress write a compressed version of the bitmap to the specified outPutStream ........*/
        FileOutputStream("$cachePath/image.png").use { outPutStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
        }

        val imagePath = File(cachePath, "image.png")
        val contentUri = FileProvider.getUriForFile(context, "com.example.hs.fileprovider", imagePath)

        /*........ Share intent ........*/
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share using"))
    }

}
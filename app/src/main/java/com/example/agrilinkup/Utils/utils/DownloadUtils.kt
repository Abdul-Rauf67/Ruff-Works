package com.example.agrilinkup.utils

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object DownloadUtils {
    suspend fun downloadAndSaveImage(context: Context, imageUrl: String, fileName: String) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream

            /*........ Creating a temp file and writing downloaded data to it ........*/
            val tempFile = File(context.externalCacheDir, "chatImage.jpeg")
            tempFile.outputStream().use { output -> input.copyTo(output) }

            /*........ Save the downloaded image to the gallery ........*/
            saveImageToGallery(context, tempFile, fileName)
        } catch (e: Exception) {
            withMainContext { context.toast("Error while downloading!") }
            e.printStackTrace()
        }
    }

    private fun saveImageToGallery(context: Context, file: File, fileName: String) {
        val imageName = "$fileName.jpeg"

        /*........ Media item meta data & Directory using MediaStore ........*/
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/HsChatImages")
        }

        val contentResolver = context.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = contentResolver.insert(imageCollection, contentValues)

        /*........ Writing data from file to Location(Uri) created by media store ........*/
        if (uri != null) {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            /*........ Notify the system that a media item has been inserted ........*/
            MediaScannerConnection.scanFile(context, arrayOf(uri.path), arrayOf("image/jpeg")) { _, _ -> }
        }
    }
}


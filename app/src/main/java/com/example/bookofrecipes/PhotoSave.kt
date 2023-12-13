package com.example.bookofrecipes

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

fun saveImageToInternalStorage(context: Context, selectedImageUri: Uri): String {
    val imageFileName = "recipe_image_${System.currentTimeMillis()}.jpg"
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    val imageFile = File(storageDir, imageFileName)

    try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(selectedImageUri)
        val outputStream: OutputStream = FileOutputStream(imageFile)
        inputStream?.copyTo(outputStream, bufferSize = 4 * 1024)
        outputStream.close()
        inputStream?.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return imageFile.absolutePath
}

package com.techpuram.leadandfollowmanagement.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "contact_image_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
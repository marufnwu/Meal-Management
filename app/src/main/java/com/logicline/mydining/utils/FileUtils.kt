package com.logicline.mydining.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object FileUtils {
    
    /**
     * Creates a temporary file from a URI
     * Used for converting image URIs to files for upload
     */
    fun createTempFileFromUri(context: Context, uri: Uri, fileName: String = "temp_image"): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "$fileName.jpg")
            
            inputStream?.use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                }
            }
            
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Gets the file name from a URI
     */
    fun getFileName(context: Context, uri: Uri): String? {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val nameIndex = it.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)
                if (it.moveToFirst() && nameIndex != -1) {
                    it.getString(nameIndex)
                } else {
                    "image_${System.currentTimeMillis()}.jpg"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "image_${System.currentTimeMillis()}.jpg"
        }
    }
    
    /**
     * Gets the file size from a URI
     */
    fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val sizeIndex = it.getColumnIndex(MediaStore.Images.Media.SIZE)
                if (it.moveToFirst() && sizeIndex != -1) {
                    it.getLong(sizeIndex)
                } else {
                    0L
                }
            } ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
    
    /**
     * Checks if the file size is within acceptable limits (e.g., 5MB)
     */
    fun isFileSizeAcceptable(context: Context, uri: Uri, maxSizeMB: Int = 5): Boolean {
        val fileSizeBytes = getFileSize(context, uri)
        val maxSizeBytes = maxSizeMB * 1024 * 1024L
        return fileSizeBytes <= maxSizeBytes
    }
    
    /**
     * Cleanup temporary files
     */
    fun cleanupTempFiles(context: Context) {
        try {
            val cacheDir = context.cacheDir
            cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("temp_image")) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.prizma_distribucija.prizma.feature_track_location.domain

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class InternalStorageManagerImpl : InternalStorageManager {

    override fun saveBitmapToInternalStorage(
        fileName: String,
        bitmap: Bitmap,
        activity: Activity
    ): Boolean {
        return try {
            activity.openFileOutput("$fileName.jpg", Context.MODE_PRIVATE).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
                true
            }

        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getUriFromInternalStorage(fileName: String, activity: Activity): Uri {
        return withContext(Dispatchers.IO) {
            val files = activity.filesDir.listFiles()
            val file = files?.single { it.nameWithoutExtension == fileName }
            Uri.fromFile(file)
        }
    }

}
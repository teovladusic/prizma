package com.prizma_distribucija.prizma.feature_track_location.domain

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri

interface InternalStorageManager {

    fun saveBitmapToInternalStorage(fileName: String, bitmap: Bitmap, activity: Activity) : Boolean

    suspend fun getUriFromInternalStorage(fileName: String, activity: Activity) : Uri
}
package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import com.prizma_distribucija.prizma.feature_track_location.domain.InternalStorageManager

class InternalStorageManagerFakeImpl : InternalStorageManager {

    override fun saveBitmapToInternalStorage(
        fileName: String,
        bitmap: Bitmap,
        activity: Activity
    ): Boolean {
        return false
    }

    override suspend fun getUriFromInternalStorage(fileName: String, activity: Activity): Uri {
        return "".toUri()
    }
}
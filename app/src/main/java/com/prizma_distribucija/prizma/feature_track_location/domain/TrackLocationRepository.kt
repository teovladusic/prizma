package com.prizma_distribucija.prizma.feature_track_location.domain

import android.net.Uri
import com.google.firebase.storage.UploadTask
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult

interface TrackLocationRepository {
    suspend fun saveBitmapToRemoteDatabase(uri: Uri, path: String): TaskResult
}
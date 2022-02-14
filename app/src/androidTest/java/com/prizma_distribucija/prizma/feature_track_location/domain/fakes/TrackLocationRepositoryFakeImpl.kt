package com.prizma_distribucija.prizma.feature_track_location.domain.fakes

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult

class TrackLocationRepositoryFakeImpl : TrackLocationRepository {

    override suspend fun saveBitmapToRemoteDatabase(uri: Uri, path: String): TaskResult {
        return TaskResult(isComplete = true, isSuccess = false, errorMessage = null)
    }
}
package com.prizma_distribucija.prizma.feature_track_location.data.repository

import android.net.Uri
import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.feature_track_location.domain.TrackLocationRepository
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import javax.inject.Inject

class TrackLocationRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService
) : TrackLocationRepository {

    override suspend fun saveBitmapToRemoteDatabase(uri: Uri, path: String): TaskResult {
        return firebaseService.saveUriInFirebaseStorage(uri, path)
    }
}
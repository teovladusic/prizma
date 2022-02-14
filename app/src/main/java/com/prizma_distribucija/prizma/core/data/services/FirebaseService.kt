package com.prizma_distribucija.prizma.core.data.services

import android.net.Uri
import com.google.firebase.storage.UploadTask
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult

interface FirebaseService {
    suspend fun getUsersByCode(code: String) : List<UserDto>

    suspend fun saveUriInFirebaseStorage(uri: Uri, path: String) : TaskResult
}
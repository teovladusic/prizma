package com.prizma_distribucija.prizma.core.data.services

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import com.prizma_distribucija.prizma.feature_track_location.domain.model.TaskResult
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseServiceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val storage: StorageReference
) : FirebaseService {

    override suspend fun getUsersByCode(code: String): List<UserDto> {
        return firebaseFirestore.collection(Constants.USERS_COLLECTION_NAME)
            .whereEqualTo("code", code)
            .get().await().toObjects(UserDto::class.java)
    }

    override suspend fun saveUriInFirebaseStorage(uri: Uri, path: String): TaskResult {
        val task = storage.child(path).putFile(uri).await().task
        return TaskResult(
            isComplete = task.isComplete,
            isSuccess = task.isSuccessful,
            errorMessage = task.exception?.message
        )
    }
}
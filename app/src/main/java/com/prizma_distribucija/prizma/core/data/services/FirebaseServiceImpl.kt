package com.prizma_distribucija.prizma.core.data.services

import com.google.firebase.firestore.FirebaseFirestore
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseServiceImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : FirebaseService {

    override suspend fun getUsersByCode(code: String): List<UserDto> {
        return firebaseFirestore.collection(Constants.USERS_COLLECTION_NAME)
            .whereEqualTo("code", code)
            .get().await().toObjects(UserDto::class.java)
    }
}
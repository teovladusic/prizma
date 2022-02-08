package com.prizma_distribucija.prizma.feature_login.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.core.util.Constants.NO_USER_FOUND_ERROR_MESSAGE
import com.prizma_distribucija.prizma.core.util.Constants.USERS_COLLECTION_NAME
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.feature_login.domain.model.UserDtoMapper
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.log

class LoginRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService,
    private val mapper: UserDtoMapper
) : LoginRepository {

    override suspend fun getUserByCode(code: String): User? {
        val users = firebaseService.getUsersByCode(code)

        return if (users.isEmpty()) {
            null
        } else {
            val userDto = users[0]
            mapper.mapFromDto(userDto)
        }
    }
}
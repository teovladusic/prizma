package com.prizma_distribucija.prizma.feature_login.data.repository

import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import com.prizma_distribucija.prizma.feature_login.domain.model.UserDtoMapper
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val firebaseService: FirebaseService,
    private val mapper: EntityMapper<UserDto, User>
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
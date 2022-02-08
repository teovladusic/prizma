package com.prizma_distribucija.prizma.core.data.services

import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto

interface FirebaseService {
    suspend fun getUsersByCode(code: String) : List<UserDto>
}
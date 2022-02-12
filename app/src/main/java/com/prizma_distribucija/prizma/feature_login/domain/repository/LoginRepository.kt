package com.prizma_distribucija.prizma.feature_login.domain.repository

import com.prizma_distribucija.prizma.core.domain.model.User

interface LoginRepository {

    suspend fun getUserByCode(code: String): User?
}
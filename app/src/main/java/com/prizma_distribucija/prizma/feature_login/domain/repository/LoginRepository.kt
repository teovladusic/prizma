package com.prizma_distribucija.prizma.feature_login.domain.repository

import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface LoginRepository {

    suspend fun getUserByCode(code: String): User?
}
package com.prizma_distribucija.prizma.feature_login.data.repository

import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository

class LoginRepoFakeImpl : LoginRepository {
    companion object {
        val userToReturn =
            User(code = "1111", lastName = "lastName", name = "name", userId = "randomId")

        const val validCode = "1111"
    }

    override suspend fun getUserByCode(code: String): User? {
        return if (code == validCode) {
            userToReturn
        } else {
            null
        }
    }
}
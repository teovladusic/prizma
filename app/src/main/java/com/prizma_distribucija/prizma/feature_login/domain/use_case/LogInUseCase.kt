package com.prizma_distribucija.prizma.feature_login.domain.use_case

import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants.NO_USER_FOUND_ERROR_MESSAGE
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LogInUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {

    operator fun invoke(code: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        val user = loginRepository.getUserByCode(code)
        if (user == null) {
            emit(Resource.Error(NO_USER_FOUND_ERROR_MESSAGE, null))
        } else {
            emit(Resource.Success(user))
        }
    }
}
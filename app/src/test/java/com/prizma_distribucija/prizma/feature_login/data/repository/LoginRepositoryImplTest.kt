package com.prizma_distribucija.prizma.feature_login.data.repository

import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import com.prizma_distribucija.prizma.feature_login.domain.model.UserDtoMapper
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import com.prizma_distribucija.prizma.rules.MainCoroutineRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class LoginRepositoryImplTest {


    @Test
    fun `get user by code, returns user`() = runBlockingTest {
        val mapper = UserDtoMapper()

        val code = "1234"
        val expectedUser =
            User(code = "1234", lastName = "LastName", name = "Name", userId = "randomId")
        val userDto =
            UserDto(code = "1234", lastName = "LastName", name = "Name", userId = "randomId")

        val firebaseService = mockk<FirebaseService>()

        coEvery { firebaseService.getUsersByCode(code) } returns listOf(userDto)

        val loginRepository = LoginRepositoryImpl(firebaseService, mapper)

        val user = loginRepository.getUserByCode(code)
        assertThat(user).isEqualTo(expectedUser)
    }

    @Test
    fun `get user by code, service returns empty list, should return null`() = runBlockingTest {
        val mapper = UserDtoMapper()

        val code = "1234"

        val firebaseService = mockk<FirebaseService>()

        coEvery { firebaseService.getUsersByCode(code) } returns emptyList()

        val loginRepository = LoginRepositoryImpl(firebaseService, mapper)

        val user = loginRepository.getUserByCode(code)
        assertThat(user).isNull()
    }
}
package com.prizma_distribucija.prizma.feature_login.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.core.util.TestDispatchers
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import com.prizma_distribucija.prizma.feature_login.domain.use_case.LogInUseCase
import com.prizma_distribucija.prizma.feature_login.presentation.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class LoginViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private var dispatcherProvider: DispatcherProvider? = null

    @Before
    fun setUp() {
        dispatcherProvider = TestDispatchers()
    }

    @After
    fun tearDown() {
        dispatcherProvider = null
    }

    @Test
    fun `on digit added, code is empty string, should add new digit`() = runBlockingTest {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle.set("code", "")
        val logInUseCase = mockk<LogInUseCase>()
        val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

        viewModel.onDigitAdded(1)
        assertThat(viewModel.code.value).isEqualTo("1")
    }

    @Test
    fun `on digit added, code has max length, code shouldn't change`() {
        val savedStateHandle = SavedStateHandle()
        savedStateHandle.set("code", "1234")
        val logInUseCase = mockk<LogInUseCase>()
        val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

        viewModel.onDigitAdded(1)
        assertThat(viewModel.code.value).isEqualTo("1234")
    }

    @Test
    fun `on digit added, code has 3 digits, should call login`() = runBlockingTest {
        val savedStateHandle = SavedStateHandle()
        val initialCode = "123"
        savedStateHandle.set("code", initialCode)

        val digitToAdd = 4

        //1234
        val codeAfterAddingLastDigit = "$initialCode$digitToAdd"

        val logInUseCase = mockk<LogInUseCase>()
        coEvery { logInUseCase(codeAfterAddingLastDigit) } returns flow { emit(Resource.Loading()) }

        val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

        val job = launch {
            viewModel.signInStatus.test {
                val emission = awaitItem()
                assertThat(emission).isInstanceOf(Resource.Loading::class.java)
                cancelAndIgnoreRemainingEvents()
            }
        }

        viewModel.onDigitAdded(digitToAdd)
        job.join()
        job.cancel()
    }

    @Test
    fun `on digit added, should call login, should observe error and get the correct message`() =
        runBlockingTest {
            val savedStateHandle = SavedStateHandle()
            val initialCode = "123"
            savedStateHandle.set("code", initialCode)

            val digitToAdd = 4

            val codeAfterAddingLastDigit = "$initialCode$digitToAdd"

            val loginRepository = mockk<LoginRepository>()
            coEvery { loginRepository.getUserByCode("1234") } returns null

            val logInUseCase = LogInUseCase(loginRepository)

            val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

            val job = launch {
                viewModel.signInStatus.test {
                    val firstEmission = awaitItem()
                    val secondEmission = awaitItem()

                    assertThat(firstEmission).isInstanceOf(Resource.Loading::class.java)
                    assertThat(secondEmission).isInstanceOf(Resource.Error::class.java)
                    assertThat(secondEmission.message).isEqualTo(Constants.NO_USER_FOUND_ERROR_MESSAGE)

                    cancelAndIgnoreRemainingEvents()
                }
            }

            viewModel.onDigitAdded(digitToAdd)
            assertThat(viewModel.code.value).isEqualTo(codeAfterAddingLastDigit)
            job.join()
            job.cancel()
        }

    @Test
    fun `on digit added, code has 3 digits, should call login, should observe success and the correct data`() =
        runBlockingTest {
            val savedStateHandle = SavedStateHandle()
            val initialCode = "123"
            savedStateHandle.set("code", initialCode)

            val digitToAdd = 4

            val userToReturn =
                User(code = "1234", lastName = "LastName", name = "Name", userId = "randomUserId")

            val codeAfterAddingLastDigit = "$initialCode$digitToAdd"

            val loginRepository = mockk<LoginRepository>()
            coEvery { loginRepository.getUserByCode("1234") } returns userToReturn

            val logInUseCase = LogInUseCase(loginRepository)

            val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

            val job = launch {
                viewModel.signInStatus.test {
                    val firstEmission = awaitItem()
                    val secondEmission = awaitItem()

                    assertThat(firstEmission).isInstanceOf(Resource.Loading::class.java)
                    assertThat(secondEmission).isInstanceOf(Resource.Success::class.java)
                    assertThat(secondEmission.data).isEqualTo(userToReturn)
                    assertThat(viewModel.code.value).isEqualTo(codeAfterAddingLastDigit)

                    cancelAndIgnoreRemainingEvents()
                }
            }
            viewModel.onDigitAdded(digitToAdd)

            job.join()
            job.cancel()
        }


    @Test
    fun `onDigitDeleted, should return when the code is empty`() {
        val savedStateHandle = SavedStateHandle()
        val initialCode = ""
        savedStateHandle.set("code", initialCode)

        val logInUseCase = mockk<LogInUseCase>()

        val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

        viewModel.onDigitDeleted()

        assertThat(viewModel.code.value).isEmpty()
    }

    @Test
    fun `onDigitDeleted, should delete one digit`() {
        val savedStateHandle = SavedStateHandle()
        val initialCode = "123"
        savedStateHandle.set("code", initialCode)

        val expectedCodeAfterDeletion = "12"

        val logInUseCase = mockk<LogInUseCase>()

        val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

        viewModel.onDigitDeleted()

        assertThat(viewModel.code.value).isEqualTo(expectedCodeAfterDeletion)
    }

    @Test
    fun `is code value set with saved state handle`() {
        val savedStateHandle = SavedStateHandle()
        val initialCode = "123"
        savedStateHandle.set("code", initialCode)

        val logInUseCase = mockk<LogInUseCase>()

        val viewModel = LoginViewModel(savedStateHandle, logInUseCase, dispatcherProvider!!)

        assertThat(viewModel.code.value).isEqualTo(initialCode)
    }
}
package com.prizma_distribucija.prizma.feature_login.domain.use_case

import com.google.common.truth.Truth.assertThat
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.Resource
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class LogInUseCaseTest {

    @Test
    fun `log in, the first emmit should be status loading`() = runBlockingTest {
        val repository = mockk<LoginRepository>()

        val code = "1234"

        coEvery { repository.getUserByCode(code) } returns null

        val logInUseCase = LogInUseCase(repository)

        val firstFlowEmission = logInUseCase(code).first()

        assertThat(firstFlowEmission).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun `log in, code is not valid, user is null, flow emits loading then error`() = runBlockingTest {
        val repository = mockk<LoginRepository>()

        val code = "1234"

        coEvery { repository.getUserByCode(code) } returns null

        val logInUseCase = LogInUseCase(repository)

        val action = logInUseCase(code)

        val lastFlowEmission = action.last()

        val firstFlowEmission = action.first()

        assertThat(lastFlowEmission).isInstanceOf(Resource.Error::class.java)
        assertThat(firstFlowEmission).isInstanceOf(Resource.Loading::class.java)
    }

    @Test
    fun `log in, code is valid, flow emits loading then success`() = runBlockingTest {
        val repository = mockk<LoginRepository>()

        val code = "1234"
        val userToReturn = User(code = code, lastName = "LastName", name = "Name", userId = "randomId")
        coEvery { repository.getUserByCode(code) } returns userToReturn

        val logInUseCase = LogInUseCase(repository)

        val action = logInUseCase(code)

        val lastFlowEmission = action.last()

        val firstFlowEmission = action.first()

        assertThat(lastFlowEmission).isInstanceOf(Resource.Success::class.java)
        assertThat(firstFlowEmission).isInstanceOf(Resource.Loading::class.java)
    }
}
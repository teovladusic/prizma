package com.prizma_distribucija.prizma.feature_login.presentation.login

import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.prizma_distribucija.prizma.feature_login.data.repository.LoginRepoFakeImpl
import com.prizma_distribucija.prizma.R
import com.prizma_distribucija.prizma.core.di.SingletonModule
import com.prizma_distribucija.prizma.core.util.Constants
import com.prizma_distribucija.prizma.core.util.safeNavigate
import com.prizma_distribucija.prizma.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
@MediumTest
@HiltAndroidTest
@UninstallModules(SingletonModule::class)
class LoginFragmentTest {

    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun onLoginSuccess_navigateToTrackLocationFragment() = runTest {
        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(this.requireView(), navController)
        }

        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.tv_num1)).perform(click())

        verify(navController).safeNavigate(
            LoginFragmentDirections.actionLoginFragmentToTrackLocationFragment(
                LoginRepoFakeImpl.userToReturn
            )
        )
    }

    @Test
    fun onLoginSuccess_loadingDialogDismiss() = runTest {
        val navController = mock(NavController::class.java)
        val loadingDialog = mock(AlertDialog::class.java)

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(this.requireView(), navController)
            this.loadingDialog = loadingDialog
        }

        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.tv_num1)).perform(click())

        verify(loadingDialog).dismiss()
        assert(loadingDialog.isShowing == false)
    }

    @Test
    fun onLoginLoading_loadingDialogShow() = runTest {
        val navController = mock(NavController::class.java)
        val loadingDialog = mock(AlertDialog::class.java)

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(this.requireView(), navController)
            this.loadingDialog = loadingDialog
        }

        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.tv_num3)).perform(click())

        verify(loadingDialog).show()
    }

    @Test
    fun onLoginError_loadingDialogDismiss_showSnackbar() = runTest {
        val navController = mock(NavController::class.java)
        val loadingDialog = mock(AlertDialog::class.java)

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(this.requireView(), navController)
            this.loadingDialog = loadingDialog
        }

        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.tv_num3)).perform(click())

        verify(loadingDialog).show()
        verify(loadingDialog).dismiss()
        assert(loadingDialog.isShowing == false)

        onView(withText(Constants.NO_USER_FOUND_ERROR_MESSAGE))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    @Test
    fun createLoadingDialog_shouldBeCreated() {
        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(this.requireView(), navController)

            try {
                loadingDialog.show()
                assert(true)
            } catch (e: Exception) {
                assert(false)
            }
        }
    }

    @Test
    fun clickOnNumberOne_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num1)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("1")))
    }

    @Test
    fun clickOnNumberTwo_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num2)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("2")))
    }

    @Test
    fun clickOnNumberThree_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num3)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("3")))
    }

    @Test
    fun clickOnNumberFour_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num4)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("4")))
    }

    @Test
    fun clickOnNumberFive_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num5)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("5")))
    }

    @Test
    fun clickOnNumberSix_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num6)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("6")))
    }

    @Test
    fun clickOnNumberSeven_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num7)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("7")))
    }

    @Test
    fun clickOnNumberEight_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num8)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("8")))
    }

    @Test
    fun clickOnNumberNine_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num9)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("9")))
    }

    @Test
    fun clickOnNumberZero_addCorrectDigitToCode() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num0)).perform(click())

        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("0")))
    }

    @Test
    fun onBackspaceClick_ShouldRemoveDigit() {
        launchFragmentInHiltContainer<LoginFragment> {
        }

        onView(withId(R.id.tv_num3)).perform(click())
        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("3")))

        onView(withId(R.id.tv_backspace)).perform(click())
        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("")))
    }

    @Test
    fun clickOnDigit_addDigitToCorrectPlace() {
        val navController = mock(NavController::class.java)

        launchFragmentInHiltContainer<LoginFragment> {
            Navigation.setViewNavController(this.requireView(), navController)
        }

        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("1")))

        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.et2)).check(ViewAssertions.matches(withText("1")))

        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.et3)).check(ViewAssertions.matches(withText("1")))

        onView(withId(R.id.tv_num1)).perform(click())
        onView(withId(R.id.et4)).check(ViewAssertions.matches(withText("1")))

        onView(withId(R.id.tv_backspace)).perform(click())
        onView(withId(R.id.et4)).check(ViewAssertions.matches(withText("")))

        onView(withId(R.id.tv_backspace)).perform(click())
        onView(withId(R.id.et3)).check(ViewAssertions.matches(withText("")))

        onView(withId(R.id.tv_backspace)).perform(click())
        onView(withId(R.id.et2)).check(ViewAssertions.matches(withText("")))

        onView(withId(R.id.tv_backspace)).perform(click())
        onView(withId(R.id.et1)).check(ViewAssertions.matches(withText("")))
    }
}
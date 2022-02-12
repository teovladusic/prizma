package com.prizma_distribucija.prizma.feature_track_location.domain

import com.google.common.truth.Truth.assertThat
import com.prizma_distribucija.prizma.core.util.TestDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TimerTests {

    private lateinit var timer: Timer
    private lateinit var dispatcherProvider: TestDispatchers

    @Before
    fun setUp() {
        dispatcherProvider = TestDispatchers()
        timer = TimerImpl(dispatcherProvider)
    }


    @Test
    fun `start counting, sets isTimerEnabled to true`() = runTest {
        assertThat(timer.isTimerEnabled).isFalse()

        timer.startCounting()

        assertThat(timer.isTimerEnabled).isTrue()
    }

    @Test
    fun `stop counting, sets isTimerEnabled to false`() {
        assertThat(timer.isTimerEnabled).isFalse()

        timer.startCounting()

        assertThat(timer.isTimerEnabled).isTrue()

        timer.stopCounting()

        assertThat(timer.isTimerEnabled).isFalse()
    }
}
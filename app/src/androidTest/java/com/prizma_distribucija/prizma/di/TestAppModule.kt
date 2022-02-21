package com.prizma_distribucija.prizma.di

import android.content.Context
import com.prizma_distribucija.prizma.feature_login.data.repository.LoginRepoFakeImpl
import com.prizma_distribucija.prizma.core.util.AndroidTestDispatchers
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import com.prizma_distribucija.prizma.feature_login.domain.use_case.LogInUseCase
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.*
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.BuildNotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun provideTestDispatchers(): DispatcherProvider = AndroidTestDispatchers()

    @Provides
    @Singleton
    fun provideFakeLogInRepository(): LoginRepository = LoginRepoFakeImpl()

    @Provides
    @Singleton
    fun provideLogInUseCase(loginRepository: LoginRepository): LogInUseCase =
        LogInUseCase(loginRepository)

    @Provides
    @Singleton
    fun provideTimer(): Timer = TimerFakeImpl()

    @Provides
    @Singleton
    fun provideLocationTracker(dispatcherProvider: DispatcherProvider): LocationTracker =
        LocationTrackerFakeImplAndroidTest(dispatcherProvider)

    @Provides
    @Singleton
    fun providePermissionManager(): PermissionManager =
        PermissionManagerFakeImpl()

    @Provides
    @Singleton
    fun provideGoogleMapManager(): GoogleMapManager = GoogleMapManagerFakeImpl()

    @Provides
    @Singleton
    fun provideBuildNotificationUseCase(
        @ApplicationContext appContext: Context
    ): BuildNotificationUseCase = BuildNotificationUseCase(appContext)

    @Provides
    @Singleton
    fun provideDistanceCalculator(dispatcherProvider: DispatcherProvider): DistanceCalculator =
        DistanceCalculatorImpl(dispatcherProvider)

    @Provides
    @Singleton
    fun provideTrackLocationRepository(): TrackLocationRepository =
        TrackLocationRepositoryFakeImpl()
}
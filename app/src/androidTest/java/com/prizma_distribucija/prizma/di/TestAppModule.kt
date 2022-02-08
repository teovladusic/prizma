package com.prizma_distribucija.prizma.di

import android.content.Context
import com.prizma_distribucija.prizma.feature_login.data.repository.LoginRepoFakeImpl
import com.prizma_distribucija.prizma.core.util.AndroidTestDispatchers
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import com.prizma_distribucija.prizma.feature_login.domain.use_case.LogInUseCase
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.GoogleMapMangerFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.LocationTrackerFakeImplAndroidTest
import com.prizma_distribucija.prizma.feature_track_location.domain.fakes.PermissionManagerFakeImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.BuildNotificationUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @ExperimentalCoroutinesApi
    @Provides
    @Singleton
    fun provideTestDispatchers() = AndroidTestDispatchers() as DispatcherProvider

    @Provides
    @Singleton
    fun provideFakeLogInRepository() = LoginRepoFakeImpl() as LoginRepository

    @Provides
    @Singleton
    fun provideLogInUseCase(loginRepository: LoginRepository) = LogInUseCase(loginRepository)

    @Provides
    @Singleton
    fun provideTimer() = TimerImpl()

    @Provides
    @Singleton
    fun provideLocationTracker(dispatcherProvider: DispatcherProvider) =
        LocationTrackerFakeImplAndroidTest(dispatcherProvider) as LocationTracker

    @Provides
    @Singleton
    fun providePermissionManager() = PermissionManagerFakeImpl() as PermissionManager

    @Provides
    @Singleton
    fun provideGoogleMapManager() = GoogleMapMangerFakeImpl() as GoogleMapManager

    @Provides
    @Singleton
    fun provideBuildNotificationUseCase(
        @ApplicationContext appContext: Context
    ) = BuildNotificationUseCase(appContext)
}
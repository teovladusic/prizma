package com.prizma_distribucija.prizma.core.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.prizma_distribucija.prizma.core.data.services.FirebaseService
import com.prizma_distribucija.prizma.core.data.services.FirebaseServiceImpl
import com.prizma_distribucija.prizma.core.domain.model.User
import com.prizma_distribucija.prizma.core.util.DefaultDispatchers
import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.core.util.EntityMapper
import com.prizma_distribucija.prizma.feature_login.data.remote.dto.UserDto
import com.prizma_distribucija.prizma.feature_login.data.repository.LoginRepositoryImpl
import com.prizma_distribucija.prizma.feature_login.domain.model.UserDtoMapper
import com.prizma_distribucija.prizma.feature_login.domain.repository.LoginRepository
import com.prizma_distribucija.prizma.feature_login.domain.use_case.LogInUseCase
import com.prizma_distribucija.prizma.feature_track_location.data.remote.dto.RouteDto
import com.prizma_distribucija.prizma.feature_track_location.data.repository.TrackLocationRepositoryImpl
import com.prizma_distribucija.prizma.feature_track_location.domain.*
import com.prizma_distribucija.prizma.feature_track_location.domain.model.Route
import com.prizma_distribucija.prizma.feature_track_location.domain.model.RouteDtoMapper
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.BuildNotificationUseCase
import com.prizma_distribucija.prizma.feature_track_location.domain.use_cases.SaveRouteToRemoteDatabaseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    @Provides
    @Singleton
    fun provideFirebaseService(
        firebaseFirestore: FirebaseFirestore,
        storageReference: StorageReference
    ) =
        FirebaseServiceImpl(firebaseFirestore, storageReference) as FirebaseService

    @Singleton
    @Provides
    fun provideFirestoreInstance() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideUserDtoMapper(): EntityMapper<UserDto, User> = UserDtoMapper()

    @Provides
    @Singleton
    fun provideLoginRepository(
        firebaseService: FirebaseService,
        mapper: EntityMapper<UserDto, User>
    ) =
        LoginRepositoryImpl(firebaseService, mapper) as LoginRepository

    @Provides
    @Singleton
    fun provideLogInUseCase(loginRepository: LoginRepository) = LogInUseCase(loginRepository)

    @Provides
    @Singleton
    fun provideDispatcherProvider() = DefaultDispatchers() as DispatcherProvider

    @Provides
    @Singleton
    fun provideTimer(dispatchers: DispatcherProvider) = TimerImpl(dispatchers) as Timer

    @Provides
    @Singleton
    fun provideLocationTracker(
        dispatchers: DispatcherProvider,
        distanceCalculator: DistanceCalculator
    ): LocationTracker =
        LocationTrackerImpl(dispatchers, distanceCalculator)

    @Provides
    @Singleton
    fun providePermissionManager() = PermissionManagerImpl() as PermissionManager

    @Provides
    @Singleton
    fun provideBuildNotificationUseCase(
        @ApplicationContext appContext: Context
    ) = BuildNotificationUseCase(appContext)

    @Provides
    @Singleton
    fun provideDistanceCalculator(dispatchers: DispatcherProvider): DistanceCalculator =
        DistanceCalculatorImpl(dispatchers)

    @Provides
    @Singleton
    fun provideTrackLocationRepository(
        firebaseService: FirebaseService,
        mapper: EntityMapper<RouteDto, Route>,
        dispatchers: DispatcherProvider
    ): TrackLocationRepository =
        TrackLocationRepositoryImpl(firebaseService, mapper, dispatchers)

    @Provides
    @Singleton
    fun provideStorageReference(): StorageReference = FirebaseStorage.getInstance().reference

    @Provides
    @Singleton
    fun provideSaveRouteToRemoteDatabaseUseCase(
        trackLocationRepository: TrackLocationRepository,
    ) = SaveRouteToRemoteDatabaseUseCase(trackLocationRepository)

    @Provides
    @Singleton
    fun provideRouteDtoMapper(): EntityMapper<RouteDto, Route> = RouteDtoMapper()
}
package com.prizma_distribucija.prizma.core.di

import com.prizma_distribucija.prizma.core.util.DispatcherProvider
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManager
import com.prizma_distribucija.prizma.feature_track_location.domain.GoogleMapManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    @FragmentScoped
    fun provideGoogleMapManager(dispatchers: DispatcherProvider) =
        GoogleMapManagerImpl(dispatchers) as GoogleMapManager
}
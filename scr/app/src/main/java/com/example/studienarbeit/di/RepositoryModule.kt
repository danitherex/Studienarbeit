package com.example.studienarbeit.di

import android.content.Context
import com.example.studienarbeit.data.repository.LocationRepositoryImpl
import com.example.studienarbeit.data.repository.MarkerRepositoryImpl
import com.example.studienarbeit.domain.repository.LocationRepository
import com.example.studienarbeit.domain.repository.MarkerRepository
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLocationRepository(
        @ApplicationContext context: Context
    ): LocationRepository = LocationRepositoryImpl(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )

    @Singleton
    @Provides
    fun provideMarkerRepository(
        database: FirebaseFirestore
    ): MarkerRepository = MarkerRepositoryImpl(database)
}
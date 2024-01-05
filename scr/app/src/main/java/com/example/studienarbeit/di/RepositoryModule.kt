package com.example.studienarbeit.di

import android.content.Context
import com.example.studienarbeit.data.repository.GoogleAuthRepositoryImpl
import com.example.studienarbeit.data.repository.LocationRepositoryImpl
import com.example.studienarbeit.data.repository.MarkerRepositoryImpl
import com.example.studienarbeit.domain.repository.GoogleAuthRepository
import com.example.studienarbeit.domain.repository.LocationRepository
import com.example.studienarbeit.domain.repository.MarkerRepository
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
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
        collection: CollectionReference
    ): MarkerRepository = MarkerRepositoryImpl(collection)

    @Singleton
    @Provides
    fun provideFirebaseAuth(
        @ApplicationContext context:Context,
        auth:FirebaseAuth
    ): GoogleAuthRepository = GoogleAuthRepositoryImpl(
        context,
        Identity.getSignInClient(context),
        auth
    )
}
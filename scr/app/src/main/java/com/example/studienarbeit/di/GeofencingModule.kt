package com.example.studienarbeit.di

import android.content.Context
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeofencingModule {
    @Singleton
    @Provides
    fun provideGeofencingClient(
    @ApplicationContext context:Context
        ): GeofencingClient = LocationServices.getGeofencingClient(context)

}
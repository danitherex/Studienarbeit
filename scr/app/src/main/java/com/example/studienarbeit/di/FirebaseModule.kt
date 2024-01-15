package com.example.studienarbeit.di

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Singleton
    @Provides
    fun provideMarkerCollection(): CollectionReference = FirebaseFirestore.getInstance().collection("Markers")


    @Singleton
    @Provides
    fun provideFirebaseAuth():FirebaseAuth = Firebase.auth
}
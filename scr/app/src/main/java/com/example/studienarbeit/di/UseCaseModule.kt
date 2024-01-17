package com.example.studienarbeit.di

import com.example.studienarbeit.domain.repository.EmailAuthRepository
import com.example.studienarbeit.domain.repository.MarkerRepository
import com.example.studienarbeit.domain.use_case.login.EmailAuthUseCases
import com.example.studienarbeit.domain.use_case.login.SignIn
import com.example.studienarbeit.domain.use_case.login.SignOut
import com.example.studienarbeit.domain.use_case.login.SignUp
import com.example.studienarbeit.domain.use_case.marker.AddMarker
import com.example.studienarbeit.domain.use_case.marker.DeleteMarker
import com.example.studienarbeit.domain.use_case.marker.GetMarkers
import com.example.studienarbeit.domain.use_case.marker.MarkerUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideMarkerUseCases(repository: MarkerRepository): MarkerUseCases = MarkerUseCases(
        addMarker = AddMarker(repository),
        deleteMarker = DeleteMarker(repository),
        getMarkers = GetMarkers(repository)
    )

    @Singleton
    @Provides
    fun provideEmailAuthUseCases(repsitory: EmailAuthRepository): EmailAuthUseCases =
        EmailAuthUseCases(
            signIn = SignIn(repsitory),
            signUp = SignUp(repsitory),
            signOut = SignOut(repsitory)
        )
}
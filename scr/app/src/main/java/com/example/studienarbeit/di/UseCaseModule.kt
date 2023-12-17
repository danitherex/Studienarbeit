package com.example.studienarbeit.di

import com.example.studienarbeit.data.repository.MarkerRepositoryImpl
import com.example.studienarbeit.domain.repository.MarkerRepository
import com.example.studienarbeit.domain.use_case.AddMarker
import com.example.studienarbeit.domain.use_case.DeleteMarker
import com.example.studienarbeit.domain.use_case.GetMarkers
import com.example.studienarbeit.domain.use_case.UseCases
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
    fun provideUseCases(repository: MarkerRepository): UseCases = UseCases(
        addMarker = AddMarker(repository),
        deleteMarker = DeleteMarker(repository),
        getMarkers = GetMarkers(repository)
    )
}
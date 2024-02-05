package com.example.studienarbeit.di

import android.content.Context
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerManagerModule {
    @Singleton
    @Provides
    fun provideWorkerManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)
}
package com.example.studienarbeit.domain.repository

import android.content.Intent
import android.content.IntentSender
import com.example.studienarbeit.presentation.screens.signin.SignInResult
import com.example.studienarbeit.presentation.screens.signin.UserData

interface GoogleAuthRepository {

    suspend fun signIn(): IntentSender?

    suspend fun signInWithIntent(intent: Intent): SignInResult

    suspend fun signOut()

    fun getSignedUser(): UserData?
}
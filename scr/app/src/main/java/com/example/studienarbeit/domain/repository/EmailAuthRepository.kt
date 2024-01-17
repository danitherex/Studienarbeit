package com.example.studienarbeit.domain.repository

import com.example.studienarbeit.presentation.signin.SignInResult
import com.example.studienarbeit.presentation.signin.UserData

interface EmailAuthRepository {

        suspend fun signIn(email: String, password: String): SignInResult

        suspend fun signUp(email: String, password: String,username:String): SignInResult

        suspend fun signOut()

        fun getSignedUser(): UserData?
}
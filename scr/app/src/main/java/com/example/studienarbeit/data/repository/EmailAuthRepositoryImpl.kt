package com.example.studienarbeit.data.repository

import com.example.studienarbeit.domain.repository.EmailAuthRepository
import com.example.studienarbeit.presentation.signin.SignInResult
import com.example.studienarbeit.presentation.signin.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class EmailAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : EmailAuthRepository {
    override suspend fun signIn(email: String, password: String): SignInResult =
        try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl.toString()
                    )
                },
                errorMessage = null
            )

        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }

    override suspend fun signUp(email: String, password: String, username: String) = try {
        val user = auth.createUserWithEmailAndPassword(email, password).await().user
        updateUsername(user, username)
        SignInResult(
            data = user?.run {
                UserData(
                    userId = uid,
                    username = displayName,
                    profilePictureUrl = photoUrl.toString()
                )
            },
            errorMessage = null
        )
    } catch (e: Exception) {
        e.printStackTrace()
        SignInResult(
            data = null,
            errorMessage = e.message
        )
    }

    override suspend fun signOut() = auth.signOut()

    override fun getSignedUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl.toString()
        )
    }

    private fun updateUsername(user: FirebaseUser?, username: String) {
        user?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .build()
        )
    }
}
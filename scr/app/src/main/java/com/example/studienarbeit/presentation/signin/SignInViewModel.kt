package com.example.studienarbeit.presentation.signin

import androidx.lifecycle.ViewModel
import com.example.studienarbeit.domain.use_case.login.EmailAuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val emailUseCases: EmailAuthUseCases,
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignedUpSuccessful = result.data != null,
                signUpError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignUpState() }
    }

    suspend fun emailSignIn(email: String, password: String) {
        val signInResult = emailUseCases.signIn(email, password)
        onSignInResult(signInResult)
    }

    suspend fun emailSignUp(email: String, password: String, username: String) {
        val signUpResult = emailUseCases.signUp(email, password, username)
        onSignInResult(signUpResult)
    }

}
package com.example.studienarbeit.presentation.signin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SignInScreen(
    state: SignUpState,
    viewModel: SignInViewModel,
    onSignUpWithGoogle: () -> Unit,
) {
    var signUp by remember { mutableStateOf(true) }

    fun switchScreen() { signUp = !signUp }

    if (signUp) {
        SignUpScreen(
            state = state,
            viewModel = viewModel,
            onSignUpWithGoogle = onSignUpWithGoogle,
            navigateToLogin = ::switchScreen
        )
    }
    else {
        LoginScreen(
            state = state,
            viewModel = viewModel,
            onLoginWithGoogle = onSignUpWithGoogle,
            navigateToSignUp = ::switchScreen
        )
    }
}
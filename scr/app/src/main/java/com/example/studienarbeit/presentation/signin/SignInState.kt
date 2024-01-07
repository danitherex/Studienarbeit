package com.example.studienarbeit.presentation.signin

data class SignInState(
    val isSignedInSuccessfull:Boolean = false,
    val signInError:String? = null,
)
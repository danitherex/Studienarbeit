package com.example.studienarbeit.presentation.signin

data class SignUpState(
    val isSignedUpSuccessful:Boolean = false,
    val signUpError:String? = null,
)
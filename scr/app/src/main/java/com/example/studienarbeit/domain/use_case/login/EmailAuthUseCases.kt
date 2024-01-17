package com.example.studienarbeit.domain.use_case.login

data class EmailAuthUseCases(
    val signIn: SignIn,
    val signUp: SignUp,
    val signOut: SignOut,
)
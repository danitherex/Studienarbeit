package com.example.studienarbeit.domain.use_case.login

import com.example.studienarbeit.domain.repository.EmailAuthRepository

class SignUp(private val repository: EmailAuthRepository) {
    suspend operator fun invoke(email: String, password: String, username: String) =
        repository.signUp(email, password, username)
}
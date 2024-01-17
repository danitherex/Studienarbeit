package com.example.studienarbeit.domain.use_case.login

import com.example.studienarbeit.domain.repository.EmailAuthRepository

class SignIn (private val repository:EmailAuthRepository) {
    suspend operator fun invoke(email:String, password:String) = repository.signIn(email, password)
}
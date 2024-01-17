package com.example.studienarbeit.domain.use_case.login

import com.example.studienarbeit.domain.repository.EmailAuthRepository

class SignOut(
    private val repository: EmailAuthRepository
) {
    suspend operator fun invoke() = repository.signOut()
}

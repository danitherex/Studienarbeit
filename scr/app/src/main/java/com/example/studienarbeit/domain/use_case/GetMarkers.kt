package com.example.studienarbeit.domain.use_case

import com.example.studienarbeit.domain.repository.MarkerRepository

class GetMarkers(
    private val repository: MarkerRepository
) {
    operator fun invoke() = repository.getMarkers()
}
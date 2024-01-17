package com.example.studienarbeit.domain.use_case.marker

import com.example.studienarbeit.domain.repository.MarkerRepository

class DeleteMarker(
    private val repository: MarkerRepository
) {
    operator fun invoke(markerId:String) = repository.deleteMarker(markerId)

}
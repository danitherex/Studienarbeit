package com.example.studienarbeit.domain.use_case

import com.example.studienarbeit.domain.repository.MarkerRepository

class AddMarker(
    private val repository: MarkerRepository
) {
    operator fun invoke(title:String, description:String, longitude:Double, latitude:Double) = repository.addMarker(title,latitude,longitude,description)
}
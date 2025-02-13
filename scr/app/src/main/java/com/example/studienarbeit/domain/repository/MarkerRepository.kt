package com.example.studienarbeit.domain.repository

import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.model.Response
import kotlinx.coroutines.flow.Flow

interface MarkerRepository {

    fun getMarkers(): Flow<Response<List<MarkerModel>>>

    fun deleteMarker(markerId: String): Flow<Response<String>>

    fun addMarker(
        title: String,
        latitude: Double,
        longitude: Double,
        description: String,
        type:String
    ): Flow<Response<String>>
}
package com.example.studienarbeit.presentation.screens.main

import com.example.studienarbeit.domain.model.Marker

sealed interface MarkersState {
    object Loading : MarkersState
    data class Success(val markers: List<Marker>?) : MarkersState
}
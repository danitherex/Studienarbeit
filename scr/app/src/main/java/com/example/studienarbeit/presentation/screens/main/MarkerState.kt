package com.example.studienarbeit.presentation.screens.main

import com.example.studienarbeit.domain.model.Marker

sealed interface MarkerState {
    object Loading : MarkerState
    data class Success(val markers: List<Marker>?) : MarkerState
}
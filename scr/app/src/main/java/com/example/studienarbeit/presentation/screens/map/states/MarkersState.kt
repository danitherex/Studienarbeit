package com.example.studienarbeit.presentation.screens.map.states

import com.example.studienarbeit.domain.model.MarkerModel

sealed interface MarkersState {
    object Loading : MarkersState
    data class Success(val markerModels: List<MarkerModel>) : MarkersState
}
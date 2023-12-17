package com.example.studienarbeit.presentation.screens.main

import com.example.studienarbeit.domain.model.Marker

data class MarkerState(
    val markers: List<Marker> = emptyList(),
)

package com.example.studienarbeit.presentation.screens.map

sealed class MarkersEvents {
    data class DeleteMarker(val id: String) : MarkersEvents()

}
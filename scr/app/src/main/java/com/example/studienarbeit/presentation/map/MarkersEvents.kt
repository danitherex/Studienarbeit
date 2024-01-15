package com.example.studienarbeit.presentation.map

sealed class MarkersEvents {
    data class DeleteMarker(val id: String) : MarkersEvents()

}
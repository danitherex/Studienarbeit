package com.example.studienarbeit.presentation.screens.main

sealed class MarkersEvents {
    data class DeleteMarker(val id: String) : MarkersEvents()

}
package com.example.studienarbeit.settings

import com.example.studienarbeit.domain.model.MarkerModel
import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val radius:Double=250.0,
    val markers:List<MarkerModel> = emptyList(),
    val geofences:List<String> = emptyList()
)

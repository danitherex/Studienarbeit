package com.example.studienarbeit.settings

import kotlinx.serialization.Serializable


@Serializable
data class AppSettings(
    val radius:Double=250.0
)

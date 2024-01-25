package com.example.studienarbeit.presentation.addMarker

import androidx.lifecycle.ViewModel
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.use_case.marker.MarkerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddMarkerViewModel @Inject constructor(
    private val markerUseCases: MarkerUseCases?
) : ViewModel() {

    private val _titleState: MutableStateFlow<String> =
        MutableStateFlow("")
    val titleState = _titleState.asStateFlow()
    private val _descriptionState: MutableStateFlow<String> =
        MutableStateFlow("")
    val descriptionState = _descriptionState.asStateFlow()
    private val _typeState: MutableStateFlow<String> =
        MutableStateFlow("RESTAURANT")
    val typeState = _typeState.asStateFlow()

    fun setTitle(title: String) {
        _titleState.value = title
    }

    fun setDescription(description: String) {
        _descriptionState.value = description
    }

    fun setType(type: String) {
        _typeState.value = type
    }

    fun addMarker(
        title: String,
        description: String,
        longitude: Double,
        latitude: Double,
        type: String
    ): Flow<Response<String>>? {
        return markerUseCases?.addMarker?.let { it(title, description, longitude, latitude, type) }

    }

}
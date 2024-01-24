package com.example.studienarbeit.presentation.addMarker

import androidx.lifecycle.ViewModel
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.use_case.marker.MarkerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddMarkerViewModel @Inject constructor(
    private val markerUseCases: MarkerUseCases?
) :ViewModel(){

    fun addMarker(title:String, description:String, longitude:Double, latitude:Double){
        markerUseCases?.addMarker?.let { it(title,description,longitude,latitude) }
    }

}
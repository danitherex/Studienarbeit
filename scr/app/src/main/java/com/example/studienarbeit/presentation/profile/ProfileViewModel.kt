package com.example.studienarbeit.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.repository.GeofencingRepository
import com.example.studienarbeit.domain.use_case.marker.MarkerUseCases
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCases: MarkerUseCases?,
    val auth: FirebaseAuth?,
    val geofencingHelper: GeofencingRepository?

) : ViewModel() {
    private var _items: MutableStateFlow<List<MarkerModel>> =
        MutableStateFlow(listOf())
    val items = _items.asStateFlow()
    private var _isFocused: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isFocused = _isFocused.asStateFlow()
    private var originalMarkers = mutableListOf<MarkerModel>()
    private var getNotesJob: Job? = null
    private var searchQuery = ""

    init {
        getNotes()
    }

    fun setFocus(focus: Boolean) {
        _isFocused.value = focus
    }

    fun searchMarkers(query: String) {
        searchQuery = query
        val filteredList = originalMarkers.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.type.contains(query, ignoreCase = true)
        }
        _items.value = filteredList
    }

    fun deleteMarker(markerId: String): Flow<Response<String>> {
        if (useCases == null)
            return MutableStateFlow(
                Response.Error(message = Exception("useCases is null"))
            )
        return useCases.deleteMarker(markerId)
    }


    private fun getNotes() {
        getNotesJob?.cancel()
        if (useCases != null)
            getNotesJob = useCases.getMarkers()
                .onEach { markers ->
                    when (markers) {
                        is Response.Success -> {
                            originalMarkers = markers.data.toMutableList()
                            searchMarkers(searchQuery)
                            if (_items.value.isEmpty())
                                _items.value = markers.data
                        }

                        is Response.Error -> {
                            Log.d("MapViewModel", "getNotes: ${markers.message}")
                        }

                        else -> {
                            Log.d("MapViewModel", "getNotes: $markers")
                        }
                    }
                }
                .launchIn(viewModelScope)
    }

}
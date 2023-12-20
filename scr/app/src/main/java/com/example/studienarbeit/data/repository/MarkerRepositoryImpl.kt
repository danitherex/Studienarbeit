package com.example.studienarbeit.data.repository

import android.util.Log
import com.example.studienarbeit.domain.model.Marker
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.repository.MarkerRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MarkerRepositoryImpl @Inject constructor(
    private val markerCollection: CollectionReference
) : MarkerRepository {
    override fun getMarkers(): Flow<Response<List<Marker>>> = callbackFlow {
        val subscription = markerCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                trySend(Response.Error(exception)).isSuccess
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(Response.Error(Exception("Snapshot is null"))).isSuccess
                return@addSnapshotListener
            }
            val markers = snapshot.documents.mapNotNull {
                it.toObject(Marker::class.java)
            }
            trySend(Response.Success(markers)).isSuccess
        }
        awaitClose { subscription.remove() }
    }

    override fun deleteMarker(markerId: String): Flow<Response<Unit>> {
        TODO("Not yet implemented")
    }

    override fun addMarker(
        title: String,
        latitude: Double,
        longitude: Double,
        description: String
    ): Flow<Response<Unit>> {
        TODO("Not yet implemented")
    }
}
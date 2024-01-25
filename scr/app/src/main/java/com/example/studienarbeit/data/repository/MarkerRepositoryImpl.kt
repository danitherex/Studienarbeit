package com.example.studienarbeit.data.repository

import android.content.Context
import com.example.studienarbeit.domain.model.MarkerModel
import com.example.studienarbeit.domain.model.Response
import com.example.studienarbeit.domain.repository.MarkerRepository
import com.example.studienarbeit.settings.datastore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MarkerRepositoryImpl @Inject constructor(
    private val markerCollection: CollectionReference,
    private val context: Context,
    private val auth: FirebaseAuth
) : MarkerRepository {
    override fun getMarkers(): Flow<Response<List<MarkerModel>>> = callbackFlow {
        val subscription = markerCollection.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                trySend(Response.Error(exception)).isSuccess
                return@addSnapshotListener
            }
            if (snapshot == null) {
                trySend(Response.Error(Exception("Snapshot is null"))).isSuccess
                return@addSnapshotListener
            }
            val markerModels = snapshot.documents.mapNotNull {
                it.toObject(MarkerModel::class.java)
            }
            launch {
                context.datastore.updateData {
                    it.copy(
                        markers = markerModels
                    )
                }
            }
            trySend(Response.Success(markerModels)).isSuccess
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
        description: String,
        type:String
    ): Flow<Response<String>> {
        return callbackFlow {
            val markerModel = MarkerModel(
                title = title,
                description = description,
                userID = auth.currentUser?.uid ?: "",
                position = GeoPoint(latitude, longitude),
                type = type
            )
            markerCollection.add(markerModel).addOnSuccessListener {
                trySend(Response.Success("Success")).isSuccess
            }.addOnFailureListener {
                trySend(Response.Error(it)).isSuccess
            }
            awaitClose { }
        }
    }
}
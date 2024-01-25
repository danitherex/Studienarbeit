package com.example.studienarbeit.domain.model

import com.example.studienarbeit.serializer.GeoPointSerializer
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import kotlinx.serialization.Serializable

@Serializable
data class MarkerModel(@DocumentId val id:String = "", val userID:String,val title:String, val description:String, @Serializable(with = GeoPointSerializer::class) val position:GeoPoint, val type:String) {
    constructor():this("","","","", GeoPoint(0.0,0.0),"")
}
package com.example.studienarbeit.domain.model

import com.google.firebase.firestore.GeoPoint

data class Marker(val title:String, val description:String,val position:GeoPoint) {
    constructor():this("","",GeoPoint(0.0,0.0))
}
package com.example.studienarbeit.domain.model

import com.google.firebase.firestore.GeoPoint

data class MarkerModel(val title:String, val description:String, val position:GeoPoint,val type:String) {
    constructor():this("","",GeoPoint(0.0,0.0),"")
}
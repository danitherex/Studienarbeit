package com.example.studienarbeit.serializer

import com.google.firebase.firestore.GeoPoint
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object GeoPointSerializer:KSerializer<GeoPoint> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GeoPoint", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): GeoPoint {
        val coordinates = decoder.decodeString().split(",")
        return GeoPoint(coordinates[0].toDouble(), coordinates[1].toDouble())
    }

    override fun serialize(encoder: Encoder, value: GeoPoint) {
        encoder.encodeString("${value.latitude},${value.longitude}")
    }

}
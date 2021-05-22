package com.example.unposto.data.firebase.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import java.time.ZonedDateTime

data class FirebasePost (
    @DocumentId
    var id: String = "",
    var userId: String  = "",
    var description: String  = "",
    var imageUrls: List<String> = listOf(),
    var createdAt: Timestamp = Timestamp.now(),
    var geolocation: GeoPoint = GeoPoint(0.0, 0.0),
) {
}
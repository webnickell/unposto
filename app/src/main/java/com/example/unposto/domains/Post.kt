package com.example.unposto.domains

import java.time.ZonedDateTime

data class Post (
    val id: String,
    val userId: String,
    val description: String,
    val imageUrl: String,
    val createdAt: ZonedDateTime,
    var geolocation: Geolocation,
)
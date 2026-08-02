package com.example.gameengine.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Player(
    @Json(name = "id") val playerId: Int,
    @Json(name = "name") val playerName: String,
    @Json(name = "points") val points: Int = 0
)
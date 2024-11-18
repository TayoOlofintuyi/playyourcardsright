package com.example.playyourcardsright.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DecksResponse(
    @Json(name = "deck_id") val decks: Deck
)
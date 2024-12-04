package com.example.playyourcardsright.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//NEW DECK +  SHUFFLE (Shuffle the Cards)
@JsonClass(generateAdapter = true)
data class Deck(
    @Json(name = "deck_id") val deckId: String,
    val shuffled: Boolean,
    val remaining: Int
)
package com.example.playyourcardsright.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Deck(
    val deck_id: String,
    val shuffled: Boolean,
    val remaining: Int
)
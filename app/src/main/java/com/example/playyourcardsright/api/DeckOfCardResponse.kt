package com.example.playyourcardsright.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeckOfCardResponse(
    val photos: PhotoResponse
)
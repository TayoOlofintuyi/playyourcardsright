package com.example.playyourcardsright.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DrawCard(
    val image: String,
    val value: String
)
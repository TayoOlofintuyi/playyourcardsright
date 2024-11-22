package com.example.playyourcardsright.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DrawCardResult(
    val cards: List<DrawCard>
)
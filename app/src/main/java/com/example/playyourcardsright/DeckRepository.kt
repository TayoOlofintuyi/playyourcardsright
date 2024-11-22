package com.example.playyourcardsright

import com.example.playyourcardsright.api.Deck
import com.example.playyourcardsright.api.DeckOfCardsApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class DeckRepository {
    private val deckApi: DeckOfCardsApi

    init {
         val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://deckofcardsapi.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        deckApi = retrofit.create()
    }

    //I am fetching from the internal i.e. where the data is
    suspend fun fetchDeck(): Deck =
        deckApi.shuffleDeck()
}
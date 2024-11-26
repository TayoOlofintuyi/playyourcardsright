package com.example.playyourcardsright.api
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

//New Deck + Shuffle the cards
interface DeckOfCardsApi {
        @GET("/api/deck/new/shuffle/?deck_count=1")
        suspend fun shuffleDeck(): Deck

        //this deck id is different from the shuffleDeck though
        @GET("/api/deck/{id}/draw/?count=1")
        suspend fun drawCard(@Path("id") deckId: String):DrawCardResult
}


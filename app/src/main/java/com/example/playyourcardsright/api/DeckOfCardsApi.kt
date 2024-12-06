package com.example.playyourcardsright.api
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path

//New Deck + Shuffle the cards
interface DeckOfCardsApi {
        @GET("/api/deck/new/shuffle/?deck_count=1")
        suspend fun getDeck(): Deck

        //this deck id is different from the shuffleDeck though
        @GET("/api/deck/{id}/draw/?count=2")
        suspend fun drawCard(@Path("id") deckId: String):DrawCardResult

        @GET("/api/deck/{id}/draw/?count=3")
        suspend fun drawThreeCard(@Path("id") deckId: String):DrawCardResult

        @GET("/api/deck/{id}/draw/?count=1")
        suspend fun drawaCard(@Path("id") deckId: String):DrawCardResult

        @GET("/api/deck/{id}/shuffle/")
        suspend fun shuffleTheDeck(@Path("id") deckId: String): Deck
}


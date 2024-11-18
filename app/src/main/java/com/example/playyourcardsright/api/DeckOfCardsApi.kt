package com.example.playyourcardsright.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


interface DeckOfCardsApi {
        @GET("deck/new/shuffle/?deck_count=1")
        suspend fun getShuffledDeck(): DeckOfCardResponse
}


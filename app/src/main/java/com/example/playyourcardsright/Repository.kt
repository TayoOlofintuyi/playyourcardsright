package com.example.playyourcardsright

import com.example.playyourcardsright.api.Deck
import com.example.playyourcardsright.api.DeckOfCardsApi
import com.example.playyourcardsright.api.DecksResponse
import com.example.playyourcardsright.api.PhotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class Repository {
    private val deckOfCards: DeckOfCardsApi
    init {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()


         val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://deckofcardsapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        deckOfCards = retrofit.create()
    }

    suspend fun fetchDecks(): DecksResponse =
        deckOfCards.getShuffledDeck().decks
}
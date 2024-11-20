package com.example.playyourcardsright

import com.example.playyourcardsright.api.DeckOfCardsApi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

class WarRepository {

    private val deckOfCardsApi: DeckOfCardsApi
     init {
         val retrofit: Retrofit = Retrofit.Builder()
             .baseUrl("https://deckofcardsapi.com//")
             .addConverterFactory(MoshiConverterFactory.create())
             .build()
         deckOfCardsApi = retrofit.create()
     }

    suspend fun fetchContents() = deckOfCardsApi.fetchContents()
}
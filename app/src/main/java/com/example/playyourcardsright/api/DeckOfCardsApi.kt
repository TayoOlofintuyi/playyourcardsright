package com.example.playyourcardsright.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

class DeckOfCardsApi {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://deckofcardsapi.com/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val deckOfCardsService = retrofit.create(DeckOfCardsService::class.java)

    suspend fun getShuffledDeck(): Deck {
        return deckOfCardsService.getShuffledDeck()
    }

    // Other methods can be added here, like drawing cards, reshuffling, etc.

    private interface DeckOfCardsService {
        @GET("deck/new/shuffle/?deck_count=1")
        suspend fun getShuffledDeck(): Deck
    }

    data class Deck(
        val deck_id: String,
        val shuffled: Boolean,
        val remaining: Int
    )
}
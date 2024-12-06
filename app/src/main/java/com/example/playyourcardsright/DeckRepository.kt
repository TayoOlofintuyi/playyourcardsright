package com.example.playyourcardsright

import com.example.playyourcardsright.api.Deck
import com.example.playyourcardsright.api.DeckOfCardsApi
import com.example.playyourcardsright.api.DrawCard
import com.example.playyourcardsright.api.DrawCardResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class DeckRepository {
    private val deckApi: DeckOfCardsApi

    init {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .callTimeout(10, TimeUnit.SECONDS)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://deckofcardsapi.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

        deckApi = retrofit.create()
    }

    // Function to fetch the deck
    suspend fun fetchDeck(): Deck = deckApi.shuffleDeck()

    // Function to draw a single card
    suspend fun drawCard(deckId: String): DrawCardResult = deckApi.drawCard(deckId)

    // Function to draw 4 cards
    suspend fun drawThreeCards(deckId: String): DrawCardResult = deckApi.drawThreeCard(deckId)
    suspend fun drawaCard(deckId: String): DrawCardResult = deckApi.drawaCard(deckId)
}

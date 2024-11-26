package com.example.playyourcardsright

import com.example.playyourcardsright.api.Deck
import com.example.playyourcardsright.api.DeckOfCardsApi
import com.example.playyourcardsright.api.DrawCard
import com.example.playyourcardsright.api.DrawCardResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
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
//             .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
             .client(okHttpClient)
            .build()

        deckApi = retrofit.create()
    }

    //I am fetching from the internal i.e. where the data is
    suspend fun fetchDeck(): Deck =
        deckApi.shuffleDeck()

    //function to draw a card
    suspend fun drawCard(deckId : String):List<DrawCard> = deckApi.drawCard(deckId).cards
}
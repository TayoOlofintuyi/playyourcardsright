package com.example.playyourcardsright

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.photogallery.databinding.ActivityWarBinding


class War : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityWarBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

//    val api = DeckOfCardsApi()
//    init {
//        lifecycleScope.launch{
//            val deck = api.getShuffledDeck()
//            val deckId = deck.deck_id
//        }
//    }
}
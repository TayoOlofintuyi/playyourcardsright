package com.example.playyourcardsright

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.playyourcardsright.api.DrawCard
import com.example.playyourcardsright.databinding.ActivityBlackjackBinding
import kotlinx.coroutines.launch

class BlackJack : AppCompatActivity() {

    private val cardViewModel: CardViewModel by viewModels()
    private var playerTotal = 0
    private var dealerTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBlackjackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playButton.setOnClickListener {
            lifecycleScope.launch {
                cardViewModel.fetchDeck.collect { deck ->
                    // Requesting 4 cards for Blackjack
                    if (deck != null) {
                        cardViewModel.drawCards(deck.deckId, 4)
                    }

                    cardViewModel.drawCard.collect { cards ->
                        Log.d("Blackjack", "Cards drawn: ${cards.size}")
                        if (cards.size >= 4) {
                            // Draw 2 cards for the player and 2 for the dealer
                            val playerCard1 = cards[0] // Player's first card
                            val playerCard2 = cards[1] // Player's second card
                            val dealerCard1 = cards[2] // Dealer's first card
                            val dealerCard2 = cards[3] // Dealer's second card

                            // Set images for the player and dealer cards
                            binding.playerCard1.load(playerCard1?.image)
                            binding.playerCard2.load(playerCard2?.image)
                            binding.dealerCard1.load(dealerCard1?.image)
                            binding.dealerCard2.load(dealerCard2?.image)

                            // Calculate totals
                            playerTotal = calculateTotal(playerCard1, playerCard2)
                            dealerTotal = calculateTotal(dealerCard1, dealerCard2)

                            // Update UI
                            binding.playerTotal.text = "Player: $playerTotal"
                            binding.dealerTotal.text = "Dealer: $dealerTotal"

                            // Handle winner
                            val result = determineWinner(playerTotal, dealerTotal)
                            binding.gameResultText.text = result
                        }
                    }
                }
            }
        }
    }

    private fun calculateTotal(card1: DrawCard?, card2: DrawCard?): Int {
        return getCardValue(card1) + getCardValue(card2)
    }

    private fun getCardValue(card: DrawCard?): Int {
        return when (card?.value) {
            "2" -> 2
            "3" -> 3
            "4" -> 4
            "5" -> 5
            "6" -> 6
            "7" -> 7
            "8" -> 8
            "9" -> 9
            "10" -> 10
            "JACK" -> 10
            "QUEEN" -> 10
            "KING" -> 10
            "ACE" -> 11
            else -> 0
        }
    }

    private fun determineWinner(playerTotal: Int, dealerTotal: Int): String {
        return when {
            playerTotal > 21 -> "Player Busts! Dealer Wins!"
            dealerTotal > 21 -> "Dealer Busts! Player Wins!"
            playerTotal > dealerTotal -> "Player Wins!"
            dealerTotal > playerTotal -> "Dealer Wins!"
            else -> "It's a Tie!"
        }
    }
}



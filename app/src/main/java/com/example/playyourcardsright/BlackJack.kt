package com.example.playyourcardsright

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.playyourcardsright.api.DrawCard
import com.example.playyourcardsright.databinding.ActivityBlackjackBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BlackJack : AppCompatActivity() {

    private val cardViewModel: CardViewModel by viewModels()
    private var playerTotal = 0
    private var dealerTotal = 0
    private var isPlayerTurn = true // Track whose turn it is

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBlackjackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.playButton.setOnClickListener {
            lifecycleScope.launch {

                cardViewModel.fetchDeck.collect { deck ->
                    if (deck != null) {
                        cardViewModel.drawCards(deck.deckId, 4)
                    }

                    cardViewModel.drawCard.collect { cards ->
                        if (cards.size >= 4) {
                            // Initialize the game by drawing the initial 4 cards
                            setupInitialCards(binding, cards)

                            // Enable Stop Button for player
                            binding.stopButton.isEnabled = true
                        }
                    }
                }
            }
        }

        binding.drawButton.setOnClickListener {
            lifecycleScope.launch {
                if (isPlayerTurn && cardViewModel.fetchDeck.value != null) {
                    cardViewModel.drawCards(cardViewModel.fetchDeck.value!!.deckId, 1)

                    // Only process the last drawn card
                    val newCard = cardViewModel.drawCard.value.lastOrNull() // Get the latest card
                    if (newCard != null) {
                        playerTotal += getCardValue(newCard)
                        updatePlayerUI(binding, newCard)

                        if (playerTotal > 21) {
                            binding.gameResultText.text = "Player Busts! Dealer Wins!"
                            endGame(binding)
                        }
                    }
                }
            }
        }

        binding.stopButton.setOnClickListener {
            isPlayerTurn = false // End player's turn
            binding.drawButton.isEnabled = false
            binding.stopButton.isEnabled = false
            lifecycleScope.launch {
                playDealerTurn(binding)
            }
        }
    }

    private fun setupInitialCards(binding: ActivityBlackjackBinding, cards: List<DrawCard?>) {
        val playerCard1 = cards[0]
        val playerCard2 = cards[1]
        val dealerCard1 = cards[2]
        val dealerCard2 = cards[3]

        // Set card images
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
    }

    private fun updatePlayerUI(binding: ActivityBlackjackBinding, card: DrawCard?) {
        binding.playerCardExtra.load(card?.image)
        binding.playerTotal.text = "Player: $playerTotal"
    }

    private suspend fun playDealerTurn(binding: ActivityBlackjackBinding) {
        val deck = cardViewModel.fetchDeck.value ?: return

        while (dealerTotal < 21 && dealerTotal < playerTotal) {
            delay(1000L)
            cardViewModel.drawCards(deck.deckId, 1)

            val newCard = cardViewModel.drawCard.value.lastOrNull() // Get the latest card
            if (newCard != null) {
                dealerTotal += getCardValue(newCard)
                updateDealerUI(binding, newCard)
                delay(1000L)

                if (dealerTotal > 21 || dealerTotal > playerTotal) {
                    break
                }
            }
        }

        determineFinalResult(binding)
    }

    private fun updateDealerUI(binding: ActivityBlackjackBinding, card: DrawCard?) {
        binding.dealerCardExtra.load(card?.image)
        binding.dealerTotal.text = "Dealer: $dealerTotal"
    }

    private fun determineFinalResult(binding: ActivityBlackjackBinding) {
        val result = determineWinner(playerTotal, dealerTotal)
        binding.gameResultText.text = result
    }

    private fun endGame(binding: ActivityBlackjackBinding) {
        binding.drawButton.isEnabled = false
        binding.stopButton.isEnabled = false
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
            dealerTotal >= playerTotal -> "Dealer Wins!"
            else -> "Player Wins!"
        }
    }
}



package com.example.playyourcardsright

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.playyourcardsright.api.DrawCard
import com.example.playyourcardsright.databinding.ActivityBlackjackBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BlackJack : AppCompatActivity() {
    private lateinit var binding: ActivityBlackjackBinding
    private val cardViewModel: CardViewModel by viewModels()
    private var playerTotal = 0
    private var dealerTotal = 0
    private var isPlayerTurn = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlackjackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.stopButton.isEnabled = false
        binding.drawButton.isEnabled = false

        binding.playButton.setOnClickListener {
            lifecycleScope.launch {
                cardViewModel.fetchDeck.collect { deck ->
                    if (deck != null) {
                        cardViewModel.drawCards(deck.deckId, 3)

                        cardViewModel.drawCard.collect { cards ->
                            if (cards.size >= 3) {
                                setupInitialCards(binding, cards)
                                binding.stopButton.isEnabled = true
                                binding.drawButton.isEnabled = true
                                binding.playButton.isEnabled = false
                            }
                        }
                    } else {
                        Log.e("Blackjack", "Deck is null. Cannot proceed with drawing cards.")
                    }
                }
            }
        }

        binding.drawButton.setOnClickListener {
            lifecycleScope.launch {
                if (isPlayerTurn) {
                    val currentDeck = cardViewModel.fetchDeck.value
                    val deckId = currentDeck?.deckId ?: return@launch
                    cardViewModel.drawCards(deckId, 1)

                    val newCard = cardViewModel.drawCard.value.lastOrNull()
                    if (newCard != null) {
                        playerTotal += getCardValue(newCard)
                        updatePlayerUI(binding, newCard)

                        if (playerTotal > 21) {
                            showGameResultPopup(
                                binding,
                                "Play your cards Right:\n Player Busts! Dealer Wins!\n Player total: $playerTotal\n Dealer total: $dealerTotal\n"
                            )
                        }
                    }
                }
            }
        }


        binding.stopButton.setOnClickListener {
            isPlayerTurn = false
            binding.drawButton.isEnabled = false
            binding.stopButton.isEnabled = false
            if (playerTotal > 21) {
                showGameResultPopup(
                    binding,
                    "Play your cards Right:\n Player Busts! Dealer Wins!\n Player total: $playerTotal\n Dealer total: $dealerTotal\n"
                )
            }
            lifecycleScope.launch {
                playDealerTurn(binding)
            }
        }
    }

    private fun setupInitialCards(binding: ActivityBlackjackBinding, cards: List<DrawCard?>) {
        val playerCard1 = cards[0]
        val playerCard2 = cards[1]
        val dealerCard1 = cards[2]


        binding.playerCard1.load(playerCard1?.image)
        binding.playerCard2.load(playerCard2?.image)
        binding.dealerCard1.load(dealerCard1?.image)


        binding.dealerCard2.setImageResource(R.drawable.back_second)

        playerTotal = calculateTotal(playerCard1, playerCard2)
        dealerTotal = calculateTotal(dealerCard1, null)

        binding.playerTotal.text = "Player: $playerTotal"
        binding.dealerTotal.text = "Dealer: $dealerTotal"


    }


    private fun updatePlayerUI(binding: ActivityBlackjackBinding, card: DrawCard?) {
        binding.playerCardExtra.load(card?.image)
        binding.playerTotal.text = "Player: $playerTotal"
    }

    private suspend fun playDealerTurn(binding: ActivityBlackjackBinding) {

        val currentDeck = cardViewModel.fetchDeck.value
        val deckId = currentDeck?.deckId ?: return
        cardViewModel.drawCards(deckId, 1)

        val dealerCard2 = cardViewModel.drawCard.value.lastOrNull()

        if (dealerCard2 != null) {
            dealerTotal += getCardValue(dealerCard2)
            binding.dealerCard2.load(dealerCard2.image)
            binding.dealerTotal.text = "Dealer: $dealerTotal"
        }


        while (dealerTotal < 21 && dealerTotal < playerTotal) {
            delay(1000L)

            val currentDeck = cardViewModel.fetchDeck.value
            val deckId = currentDeck?.deckId ?: return
            cardViewModel.drawCards(deckId, 1)

            val newCard = cardViewModel.drawCard.value.lastOrNull()
            if (newCard != null) {
                dealerTotal += getCardValue(newCard)
                updateDealerUI(binding, newCard)
                delay(1000L)

                if (dealerTotal > 21 || dealerTotal > playerTotal) {
                    break
                }
            }
        }

        val result = determineWinner()
        showGameResultPopup(binding, result)
    }


    private fun updateDealerUI(binding: ActivityBlackjackBinding, card: DrawCard?) {
        binding.dealerCardExtra.load(card?.image)
        binding.dealerTotal.text = "Dealer: $dealerTotal"
    }

    private fun showGameResultPopup(binding: ActivityBlackjackBinding, result: String) {
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Game Over")
            .setMessage(result)
            .setPositiveButton("Play Again") { _, _ ->
                resetGame(binding)
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .setNeutralButton("Share") { _, _ ->
                checkContactPermissionAndShare(result)
                resetGame(binding)
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    private fun checkContactPermissionAndShare(result: String) {
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            shareGameResult(result)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), 1001)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareGameResult(determineWinner())
        } else {
            Toast.makeText(
                this,
                "Contacts permission is required to share the result.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun shareGameResult(result: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Game Result: $result")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Game Result"))
        resetGame(binding)
    }

    private fun resetGame(binding: ActivityBlackjackBinding) {
        playerTotal = 0
        dealerTotal = 0
        isPlayerTurn = true

        binding.playerTotal.text = "Player: $playerTotal"
        binding.dealerTotal.text = "Dealer: $dealerTotal"

        binding.playerCard1.setImageResource(R.drawable.back_second)
        binding.playerCard2.setImageResource(R.drawable.back_second)
        binding.dealerCard1.setImageResource(R.drawable.back_second)
        binding.dealerCard2.setImageResource(R.drawable.back_second)
        binding.playerCardExtra.setImageResource(R.drawable.back_second)
        binding.dealerCardExtra.setImageResource(R.drawable.back_second)

        binding.playButton.isEnabled = true
        binding.drawButton.isEnabled = false
        binding.stopButton.isEnabled = false


        lifecycleScope.launch {
            val currentDeck = cardViewModel.fetchDeck.value
            val deckId = currentDeck?.deckId ?: return@launch
            cardViewModel.shuffleDeck(deckId)
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

    private fun determineWinner(): String {
        return when {
            playerTotal > 21 -> "Play your cards Right:\nPlayer Busts! Dealer Wins!\n Player total: $playerTotal\n Dealer total: $dealerTotal"
            dealerTotal > 21 -> "Play your cards Right:\nDealer Busts! Player Wins!\n Player total: $playerTotal\n Dealer total: $dealerTotal"
            dealerTotal >= playerTotal -> "Play your cards Right:\nDealer Wins!\n Player total: $playerTotal\n Dealer total: $dealerTotal"
            else -> "Play your cards Right:\nPlayer Wins!\n Player total: $playerTotal\n Dealer total: $dealerTotal"
        }
    }
}

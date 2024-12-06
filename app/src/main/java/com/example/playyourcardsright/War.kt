package com.example.playyourcardsright

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.playyourcardsright.api.DrawCard

import com.example.playyourcardsright.databinding.ActivityWarBinding
import kotlinx.coroutines.launch

class War : AppCompatActivity() {
    private val cardViewModel: CardViewModel by viewModels()
    private var player1Wins = 0
    private var player2Wins = 0
    private var tieCount = 0
    private var roundsPlayed = 0
    private var deckId: String? = null
    private var isCollectingCards = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWarBinding.inflate(layoutInflater)
        setContentView(binding.root)



        lifecycleScope.launch {
            cardViewModel.fetchDeck.collect { deck ->
                deckId = deck?.deckId
                deckId?.let { cardViewModel.shuffleDeck(it) }
                if (deck != null) {
                    binding.playButton.isEnabled = true
                }
            }
        }

        binding.playButton.setOnClickListener {
            isCollectingCards = true
            if (deckId != null && roundsPlayed < 26) {
                lifecycleScope.launch {
                    cardViewModel.drawCards(deckId!!, 2)

                    cardViewModel.drawCard.collect { cards ->
                        if (isCollectingCards) {
                            if (cards.size >= 2) {
                                val player1Card = cards[0]
                                val player2Card = cards[1]

                                player1Card?.let {
                                    binding.player1Card.load(it.image)
                                }
                                player2Card?.let {
                                    binding.player2Card.load(it.image)
                                }

                                val roundResult = compareCards(player1Card, player2Card)
                                binding.gameResultText.text = roundResult
                                updateWinCounters(binding)

                                if (roundsPlayed == 26) {
                                    declareOverallWinner(binding)
                                    binding.playButton.isEnabled = false
                                }
                            }
                        }
                        isCollectingCards = false
                    }
                }
            }
        }

        binding.resetButton.setOnClickListener {
            resetGame(binding)
        }
    }

    private fun compareCards(player1Card: DrawCard?, player2Card: DrawCard?): String {
        val player1CardValue = getCardValue(player1Card)
        val player2CardValue = getCardValue(player2Card)

        roundsPlayed++

        return when {
            player1CardValue > player2CardValue -> {
                player1Wins++
                "Player 1 Wins this round!"
            }

            player1CardValue < player2CardValue -> {
                player2Wins++
                "Computer Wins this round!"
            }

            else -> {
                tieCount++
                "It's a Tie!"
            }
        }
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
            "JACK" -> 11
            "QUEEN" -> 12
            "KING" -> 13
            "ACE" -> 14
            else -> 0
        }
    }

    private fun updateWinCounters(binding: ActivityWarBinding) {
        binding.player1Wins.text = "Player 1 Wins: $player1Wins"
        binding.player2Wins.text = "Computer Wins: $player2Wins"
        binding.tieCount.text = "Ties: $tieCount"
    }

    private fun declareOverallWinner(binding: ActivityWarBinding) {
        val overallWinner = when {
            player1Wins > player2Wins -> "Play Your Cards Right:\n Player 1 is the Overall Winner!\n Player Wins: $player1Wins\n Computer Wins: $player2Wins\n Ties: $tieCount"
            player1Wins < player2Wins -> "Play Your Cards Right:\n Computer is the Overall Winner!\n Player Wins: $player1Wins\n Computer Wins: $player2Wins\n Ties: $tieCount"
            else -> "Play Your Cards Right:\n It's a Tie!\n Player Wins: $player1Wins\n Computer Wins: $player2Wins\n Ties: $tieCount"
        }
        showGameResultPopup(binding, overallWinner)
    }


    private fun showGameResultPopup(binding: ActivityWarBinding, result: String) {
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            shareGameResult(
                when {
                    player1Wins > player2Wins -> "Play Your Cards Right:\n Player 1 is the Overall Winner!\n Player Wins: $player1Wins\n Computer Wins: $player2Wins\n Ties: $tieCount"
                    player1Wins < player2Wins -> "Play Your Cards Right:\n Computer is the Overall Winner!\n Player Wins: $player1Wins\n Computer Wins: $player2Wins\n Ties: $tieCount"
                    else -> "Play Your Cards Right: It's a Tie!\n Player Wins: $player1Wins\n Computer Wins: $player2Wins\n Ties: $tieCount"
                }
            )
        } else {
            Toast.makeText(
                this,
                "Contacts permission is required to share the result.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkContactPermissionAndShare(result: String) {
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            shareGameResult(result)
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), 1001)
        }
    }

    private fun shareGameResult(result: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Game Result: $result")
        }
        startActivity(Intent.createChooser(shareIntent, "Share Game Result"))
    }

    private fun resetGame(binding: ActivityWarBinding) {
        player1Wins = 0
        player2Wins = 0
        roundsPlayed = 0
        tieCount = 0
        isCollectingCards = false

        binding.player1Card.setImageResource(R.drawable.back_second)
        binding.player2Card.setImageResource(R.drawable.back_second)
        binding.player1Wins.text = "Player 1 Wins: 0"
        binding.player2Wins.text = "Computer Wins: 0"
        binding.tieCount.text = "Ties: 0"
        binding.gameResultText.text = "Result will be shown here"
        binding.playButton.isEnabled = false


        deckId?.let {
            lifecycleScope.launch {
                cardViewModel.shuffleDeck(it)
                binding.playButton.isEnabled = true
            }
        }
    }


}





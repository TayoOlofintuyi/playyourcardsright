package com.example.playyourcardsright

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.playyourcardsright.api.DrawCard
import com.example.playyourcardsright.databinding.ActivityWarBinding
import kotlinx.coroutines.launch

class War : AppCompatActivity() {
    private var result: String? = null
    private val cardViewModel: CardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWarBinding.inflate(layoutInflater)
        setContentView(binding.root)




        binding.playButton.setOnClickListener {
            lifecycleScope.launch {

                cardViewModel.fetchDeck.collect { deck ->
                    Log.d("FetchDeck War.kt", "Deck fetched: $deck")

                    // Fetch the cards to be dealt
                    cardViewModel.drawCard.collect { cards ->
                        Log.d("Drawn cards","$cards")
                        if (cards.size >=2) {
                            val player1Card = cards[0] // Card for Player 1
                            val player2Card = cards[1] // Card for Player 2

                            Log.d("Player1Card", "$player1Card")
                            Log.d("Player2Card", "$player2Card")
                            // Ensure cards are not null
                            player1Card?.let {
                                binding.player1Card.load(it.image) // Set image for Player 1's card
                            }
                            player2Card?.let {
                                binding.player2Card.load(it.image) // Set image for Player 2's card
                            }

                            // Compare the cards and update the result
                            val result = compareCards(player1Card, player2Card)
                            binding.gameResultText.text = result
                        }
                    }
                }
            }
        }
    }

    private fun compareCards(player1Card: DrawCard?, player2Card: DrawCard?): String {
        val player1CardValue = getCardValue(player1Card)
        val player2CardValue = getCardValue(player2Card)

        return when {
            player1CardValue > player2CardValue -> {
                result = "Player 1 Wins!" // Set the result
                "Player 1 Wins!"
            }
            player1CardValue < player2CardValue -> {
                result = "Player 2 Wins!" // Set the result
                "Player 2 Wins!"
            }
            else -> {
                result = "It's a Tie!" // Set the result
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
            else -> 0 // Default value in case of unknown rank
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_war, menu)
        return true
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.war_menu -> {

                // Check if there's a result and pass it to ResultActivity
                result?.let {
                    Log.d("War Activity", "Game result: $it")
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("gameResult", it) // Pass the result as extra
                    startActivity(intent)
                } ?: run {
                    Log.d("War Activity", "No result to display")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

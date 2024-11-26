package com.example.playyourcardsright
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.playyourcardsright.databinding.ActivityWarBinding
import kotlinx.coroutines.launch


class War : AppCompatActivity() {

    private val cardViewModel: CardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.playButton.setOnClickListener {
            lifecycleScope.launch {
//                val deck = DeckRepository().fetchDeck()
//                Log.d("War", "Response 1; $deck")
                cardViewModel.fetchDeck.collect{
                    deck -> Log.d("War", "Response 2; $deck")
                    cardViewModel.drawCard.collect { cards ->
                        if (cards.isNotEmpty()) {
                            val card = cards[0]
                            card?.let {
                                binding.player1Card.load(card.image)
                            }
                        }
                    }
                }

            }
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
                // Handle the action for viewing the game history
                Log.d("War Activity", "View Results clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}


package com.example.playyourcardsright
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.playyourcardsright.databinding.ActivityWarBinding
import kotlinx.coroutines.launch


class War : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityWarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.placeButton.setOnClickListener {
            val openDeckRepository = DeckRepository()
            lifecycleScope.launch {
                val deck = openDeckRepository.fetchDeck()
                val drawCard = openDeckRepository.drawCard()
                Log.d("War Activity", "Deck information to create, and shuffle: $drawCard")
            }
        }

    }

}
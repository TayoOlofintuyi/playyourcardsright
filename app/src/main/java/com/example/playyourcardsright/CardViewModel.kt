package com.example.playyourcardsright

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playyourcardsright.api.Deck
import com.example.playyourcardsright.api.DrawCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardViewModel : ViewModel() {

    private val deckRepository = DeckRepository()

    private val _fetchDeck: MutableStateFlow<Deck?> = MutableStateFlow(null)
    val fetchDeck: StateFlow<Deck?>
        get() = _fetchDeck.asStateFlow()

    private val _drawCard: MutableStateFlow<List<DrawCard?>> = MutableStateFlow(emptyList())
    val drawCard: StateFlow<List<DrawCard?>>
        get() = _drawCard.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val deck = deckRepository.fetchDeck()
                _fetchDeck.value = deck
                // Default behavior can draw 4 cards for Blackjack
                drawCards(deck.deckId, 4)
            } catch (e: Exception) {
                Log.e("CardViewModel", "Error fetching: ${e.message}")
            }
        }
    }

    // Function to shuffle deck
    suspend fun shuffleDeck(): Deck {
        val shuffledDeck = deckRepository.fetchDeck()
        _fetchDeck.value = shuffledDeck
        return shuffledDeck
    }

    // Function to draw a specified number of cards
    suspend fun drawCards(deckId: String, count: Int) {
        try {
            val result = if (count == 4) {
                deckRepository.drawFourCards(deckId) // For Blackjack, 4 cards
            } else {
                if (count == 1) {
                    deckRepository.drawaCard(deckId)
                }else {
                    deckRepository.drawCard(deckId) // For War, 2 cards
                }
            }
            _drawCard.value = result.cards // Save the drawn cards to state
            Log.d("CardViewModel", "$count Cards drawn: ${result.cards}")
        } catch (e: Exception) {
            Log.e("CardViewModel", "Error drawing cards: ${e.message}")
        }
    }
}


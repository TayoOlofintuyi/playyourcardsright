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

class CardViewModel(): ViewModel() {

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
                Log.d("War", "Before call")
                val deck = deckRepository.fetchDeck()
                Log.d("War", "After call")
                _fetchDeck.value = deck
                Log.d("FetchDeck in ViewModel", "Response; $deck")
                val cards = deckRepository.drawCard(deck.deckId)
                _drawCard.value = cards
                Log.d("DrawCard in ViewModel", "Response Cards; $cards")
            }
            catch (e:Exception){
                Log.e("CardViewModel", "Error fetching: ${e.message}")
            }

        }
    }

    suspend fun shuffleDeck(): Deck {
        // API call to shuffle deck and return the shuffled deck object
        val shuffledDeck = deckRepository.fetchDeck()
        _fetchDeck.value = shuffledDeck
        Log.d("ShuffledDeck", "Value is $shuffledDeck")
        return shuffledDeck
    }

    // Function to draw two cards
    suspend fun drawTwoCards(deckId: String) {
        try {
            // Draw two cards from the deck
            val cards = mutableListOf<DrawCard>()
            cards.addAll(drawCard(deckId))  // First card
            cards.addAll(drawCard(deckId))  // Second card

            // Update the _drawCard state with the drawn cards
            _drawCard.value = cards

            Log.d("CardViewModel", "Cards drawn: $cards")
        } catch (e: Exception) {
            Log.e("CardViewModel", "Error drawing cards: ${e.message}")
        }
    }

    suspend fun drawCard(deckId: String): List<DrawCard> {
        val deck = deckRepository.fetchDeck()
        _fetchDeck.value = deck
        return deckRepository.drawCard(deck.deckId)
    }


}
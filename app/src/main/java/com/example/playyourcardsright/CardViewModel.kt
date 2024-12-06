package com.example.playyourcardsright

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playyourcardsright.api.Deck
import com.example.playyourcardsright.api.DrawCard
import kotlinx.coroutines.delay
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

                drawCards(deck.deckId, 3)
            } catch (e: Exception) {
                Log.e("CardViewModel", "Error fetching: ${e.message}")
            }
        }
    }



    suspend fun shuffleDeck(): Deck {
        val shuffledDeck = deckRepository.fetchDeck()
        _fetchDeck.value = shuffledDeck
        return shuffledDeck
    }


    suspend fun drawCards(deckId: String, count: Int) {
        try {
            val result = when (count) {
                3 -> deckRepository.drawThreeCards(deckId)
                2 -> deckRepository.drawCard(deckId)
                1 -> deckRepository.drawaCard(deckId)
                else -> deckRepository.drawCard(deckId)
            }

            _drawCard.value = result.cards


            val currentDeck = _fetchDeck.value
            if (currentDeck != null) {
                val updatedDeck = currentDeck.copy(remaining = currentDeck.remaining - count)
                _fetchDeck.value = updatedDeck
            }


            if (_fetchDeck.value?.remaining ?: 0 <= 0) {
                fetchNewDeck()

                delay(500)
                drawCards(_fetchDeck.value?.deckId ?: "", count)
            }

            Log.d("CardViewModel", "$count Cards drawn: ${result.cards}, Remaining: ${_fetchDeck.value?.remaining}")
        } catch (e: Exception) {
            Log.e("CardViewModel", "Error drawing cards: ${e.message}")
        }
    }



    fun fetchNewDeck() {
        viewModelScope.launch {
            try {
                val newDeck = deckRepository.fetchDeck()
                _fetchDeck.value = newDeck
                Log.d("CardViewModel", "Fetched a new deck: ${newDeck.deckId}")
            } catch (e: Exception) {
                Log.e("CardViewModel", "Error fetching new deck: ${e.message}")
            }
        }
    }

}


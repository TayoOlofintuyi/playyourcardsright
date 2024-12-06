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
    private var deckremaining = 0

    private val _fetchDeck: MutableStateFlow<Deck?> = MutableStateFlow(null)
    val fetchDeck: StateFlow<Deck?>
        get() = _fetchDeck.asStateFlow()

    private val _drawCard: MutableStateFlow<List<DrawCard?>> = MutableStateFlow(emptyList())
    val drawCard: StateFlow<List<DrawCard?>>
        get() = _drawCard.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val deck = deckRepository.getDeck()
                deckremaining = 52
                _fetchDeck.value = deck


            } catch (e: Exception) {
                Log.e("CardViewModel", "Error fetching: ${e.message}")
            }
        }
    }



    suspend fun shuffleDeck(deckId: String) {
        deckRepository.shuffleTheDeck(deckId)

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
            deckremaining -= count


//            val currentDeck = _fetchDeck.value
//            if (currentDeck != null) {
//                val updatedDeck = currentDeck.copy(remaining = currentDeck.remaining - count)
//                _fetchDeck.value = updatedDeck
//            }


            if (deckremaining < 0) {
                shuffleDeck(deckId)
                deckremaining = 52
                delay(500)
                drawCards(_fetchDeck.value?.deckId ?: "", count)
            }

            Log.d("CardViewModel", "$count Cards drawn: ${result.cards}, Remaining: $deckremaining")
        } catch (e: Exception) {
            Log.e("CardViewModel", "Error drawing cards: ${e.message}")
        }
    }



    fun fetchNewDeck() {
        viewModelScope.launch {
            try {
                val newDeck = deckRepository.getDeck()
                _fetchDeck.value = newDeck
                Log.d("CardViewModel", "Fetched a new deck: ${newDeck.deckId}")
            } catch (e: Exception) {
                Log.e("CardViewModel", "Error fetching new deck: ${e.message}")
            }
        }
    }

}


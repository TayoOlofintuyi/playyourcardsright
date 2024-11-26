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
                Log.d("War", "Response; $deck")
                val cards = deckRepository.drawCard(deck.deckId)
                _drawCard.value = cards
                Log.d("War", "Response Cards; $cards")
            }
            catch (e:Exception){
                Log.e("CardViewModel", "Error fetching: ${e.message}")
            }

        }
    }

}
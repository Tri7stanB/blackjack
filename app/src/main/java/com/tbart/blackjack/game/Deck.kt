package com.tbart.blackjack.game

import com.tbart.blackjack.model.Card
import com.tbart.blackjack.model.Rank
import com.tbart.blackjack.model.Suit

class Deck {
    private val deck = mutableListOf<Card>()

    fun createDeck() {
        for (i in 0..5) {
            for (suit in Suit.entries) {
                for (rank in Rank.entries) {
                    deck.add(Card(suit, rank))
                }
            }
        }
    }

    fun shuffle(){
        deck.shuffle()
    }

    fun drawCard() : Card {
        return deck.removeAt(0)
    }

    fun remainingCards() : Int = deck.size
}

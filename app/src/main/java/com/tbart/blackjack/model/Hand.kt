package com.tbart.blackjack.model

class Hand {
    private val cards = mutableListOf<Card>()

    fun addCard(card: Card, revealed: Boolean){
        card.revealed = revealed
        cards.add(card)
    }

    fun getCardsRevealed(): List<Card> {
        return cards.filter { it.revealed }
    }

    fun getAllCards(): List<Card> {
        return cards
    }


    fun getScore() : Int {
        var total = 0
        var aces = 0

        for (card in cards){
            if (!card.revealed) continue
            total += card.rank.value
            if (card.rank==Rank.ACE) aces++
        }

        while (total > 21 && aces > 0) {
            total -= 10
            aces--
        }
        return total
    }

    fun clear() {
        cards.clear()
    }

}
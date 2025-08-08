package com.tbart.blackjack.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tbart.blackjack.model.Card
import com.tbart.blackjack.model.Dealer
import com.tbart.blackjack.model.Player
import kotlinx.coroutines.delay

class BlackjackGame {
    private val deck = Deck()
    val player = Player("Joueur", 1500)
    private val dealer = Dealer()
    var manche = 1

    var mise: Int = 100
        private set

    var playerScore by mutableIntStateOf(0)
        private set

    var dealerScore by mutableIntStateOf(0)
        private set

    fun placeBet(amount: Int): Boolean {
        return if (amount > 0 && amount <= player.money) {
            mise = amount
            true
        } else {
            false
        }
    }

    fun startGame(){
        player.hand.clear()
        dealer.hand.clear()
        deck.createDeck()
        deck.shuffle()
        manche++
        player.money -= mise


        repeat(2) {
            player.hand.addCard(deck.drawCard(), revealed = true)
        }
        dealer.hand.addCard(deck.drawCard(), true)
        dealer.hand.addCard(deck.drawCard(), false)

        if(checkForThreeCardsFlush()) {
            player.money += mise*30
        } else if(checkForBrelan()) {
            player.money += mise*20
        } else if(checkForSuite()) {
            player.money += mise*10
        } else if(checkForColor()) {
            player.money += mise*5
        }

        updateScores()

    }

    fun updateScores() {
        playerScore = player.hand.getScore()
        dealerScore = dealer.hand.getScore()
    }

    fun playerHits() {
        player.hand.addCard(deck.drawCard(), true)
        updateScores()
    }

    suspend fun dealerTurn() {
        dealer.hand.getAllCards()[1].revealed = true
        updateScores()
        delay(1500)

        while (dealer.hand.getScore() < 17) {
            delay(1000)
            dealer.hand.addCard(deck.drawCard(), true)
            updateScores()
        }
    }

    fun checkForBlackjackForPlayer(): Boolean {
        return player.hand.getScore() == 21
    }

    fun checkForBlackjackForDealer(): Boolean {
        return dealer.hand.getScore() == 21
    }

    fun checkForThreeCardsFlush(): Boolean {
        val playerCards = player.hand.getCardsRevealed().toMutableList()
        playerCards.addAll(dealer.hand.getCardsRevealed())

        val groupsBySuit = playerCards.groupBy { it.suit }

        groupsBySuit.forEach { (_, cards) ->
            if (cards.size >= 3) {
                val sortedRanks = cards.map { it.rank.sequenceValue() }.sorted()

                for (i in 0..sortedRanks.size - 3) {
                    if (sortedRanks[i] + 1 == sortedRanks[i + 1] &&
                        sortedRanks[i] + 2 == sortedRanks[i + 2]) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun checkForBrelan(): Boolean {
        val playerCards = player.hand.getCardsRevealed().toMutableList()
        playerCards.addAll(dealer.hand.getCardsRevealed())
        if (playerCards[0].rank.sequenceValue() == playerCards[1].rank.sequenceValue() &&
            playerCards[1].rank.sequenceValue() == playerCards[2].rank.sequenceValue()) {
            return true
        }
        return false
    }

    fun checkForSuite(): Boolean {
        val playerCards = player.hand.getCardsRevealed().toMutableList()
        playerCards.addAll(dealer.hand.getCardsRevealed())
        playerCards.sortBy { it.rank.sequenceValue() }
        for (i in 0 until playerCards.size - 2) {
            if (playerCards[i].rank.sequenceValue() + 1 == playerCards[i + 1].rank.sequenceValue() &&
                playerCards[i + 1].rank.sequenceValue() + 1 == playerCards[i + 2].rank.sequenceValue()) {
                return true
            }
        }
        return false
    }

    fun checkForColor(): Boolean {
        val playerCards = player.hand.getCardsRevealed().toMutableList()
        playerCards.addAll(dealer.hand.getCardsRevealed())

        val groupsBySuit = playerCards.groupBy { it.suit }

        groupsBySuit.forEach { (_, cards) ->
            if (cards.size == 3) {
                return true
            }
        }
        return false
    }

    fun getPlayerCards(): List<Card> = player.hand.getCardsRevealed()
    fun getDealerCards(): List<Card> = dealer.hand.getCardsRevealed()

    fun isPlayerBusted(): Boolean = playerScore > 21
    fun isDealerBusted(): Boolean = dealerScore > 21

    fun determineWinner(): Int {
        val playerScore = playerScore
        val dealerScore = dealerScore

        return when {
            playerScore > 21 -> 2
            dealerScore > 21 -> 1
            playerScore > dealerScore -> 1
            dealerScore > playerScore -> 2
            else -> 0
        }
    }

    fun handleWin(multiplier: Int) {
        player.money += mise * multiplier
    }

    fun handleDraw() {
        player.money += mise
    }
}
package com.tbart.blackjack.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

    var playerScore by mutableIntStateOf(0)
        private set

    var dealerScore by mutableIntStateOf(0)
        private set

    fun startGame(){
        player.hand.clear()
        dealer.hand.clear()
        deck.createDeck()
        deck.shuffle()
        manche++

        repeat(2) {
            player.hand.addCard(deck.drawCard(), revealed = true)
        }
        dealer.hand.addCard(deck.drawCard(), true)
        dealer.hand.addCard(deck.drawCard(), false)

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
}
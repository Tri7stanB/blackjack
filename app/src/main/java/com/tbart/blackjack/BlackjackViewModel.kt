package com.tbart.blackjack

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tbart.blackjack.game.BlackjackGame

class BlackjackViewModel : ViewModel() {
    // L'instance du jeu survit ici au changement d'écran
    val game = BlackjackGame()

    // On déplace les états de l'interface ici aussi
    var gameOver by mutableStateOf(false)
    var message by mutableStateOf("")
    var winner by mutableIntStateOf(0)
    var selected21plus3 by mutableStateOf(false)

    init {
        // Optionnel : démarrer la partie au premier lancement
        if (game.getPlayerCards().isEmpty()) {
            game.startGame()
        }
    }
}
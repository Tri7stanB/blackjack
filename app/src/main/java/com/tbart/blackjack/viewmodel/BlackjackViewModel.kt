package com.tbart.blackjack.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.tbart.blackjack.data.manager.DailyMoneyManager
import com.tbart.blackjack.data.model.DailyRecord
import com.tbart.blackjack.game.BlackjackGame

@RequiresApi(Build.VERSION_CODES.O)
class BlackjackViewModel(application: Application) : AndroidViewModel(application) {
    private val moneyManager = DailyMoneyManager(application)

    // L'instance du jeu survit ici au changement d'écran
    val game = BlackjackGame()

    // On déplace les états de l'interface ici aussi
    var gameOver by mutableStateOf(false)
    var message by mutableStateOf("")
    var winner by mutableIntStateOf(0)
    var selected21plus3 by mutableStateOf(false)
    var waitingForBet by mutableStateOf(true)  // ← AJOUTE CETTE LIGNE


    init {
        Log.d("BLACKJACK_DEBUG", "🎮 ViewModel créé - waitingForBet=$waitingForBet")
        game.player.money = moneyManager.getCurrentMoney()
        Log.d("BLACKJACK_DEBUG", "💰 Argent chargé: ${game.player.money}")

        if (game.getPlayerCards().isEmpty()) {
            Log.d("BLACKJACK_DEBUG", "⚠️ Pas de cartes - NE PAS démarrer automatiquement")
        }
    }

    // Sauvegarder l'argent à chaque changement
    fun updateMoney(newAmount: Int) {
        game.player.money = newAmount
        moneyManager.saveCurrentMoney(newAmount)
    }

    // Récupérer l'historique
    fun getDailyHistory(): List<DailyRecord> {
        return moneyManager.getDailyHistory()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun checkForNewDay() {
        moneyManager.checkAndResetIfNewDay()
        game.player.money = moneyManager.getCurrentMoney()
    }

}

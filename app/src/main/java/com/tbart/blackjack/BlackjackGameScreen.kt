package com.tbart.blackjack

import android.renderscript.ScriptGroup.Input
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DisplayMode.Companion.Input
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tbart.blackjack.game.BlackjackGame
import kotlinx.coroutines.launch

@Composable
fun BlackjackGameScreen(modifier: Modifier = Modifier) {
    val game = remember { BlackjackGame() }
    var gameOver by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var winner by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    var selected21plus3 by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        game.startGame()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("🧑 Joueur", fontSize = 20.sp)
        Text("Argent : ${game.player.money}", fontSize = 20.sp)
        Text("Cartes: ${game.getPlayerCards().joinToString { "${it.rank} ${it.suit} " }}")
        Text("Score: ${game.playerScore}")

        Spacer(modifier = Modifier.height(20.dp))

        Text("🤵 Croupier", fontSize = 20.sp)
        Text("Cartes: ${game.getDealerCards().joinToString { "${it.rank} ${it.suit} " }}")
        Text("Score: ${game.dealerScore}")

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    game.playerHits()
                    if (game.isPlayerBusted()) {
                        gameOver = true
                        winner = game.determineWinner()
                        if (winner == 1) {
                            message = "Gagné ! Tu as battu le méchant croupier."
                            game.handleWin(2)
                        } else if (winner == 2) {
                            message = "Perdu ! Le méchant croupier vole ton argent"
                        } else {
                            message = "Egalité !"
                            game.handleDraw()
                        }
                    }
                    Log.d("DEBUG", "Draw button pressed")
                    Log.d("DEBUG", "Player cards: ${game.getPlayerCards()}")
                    Log.d("DEBUG", "Player score: ${game.playerScore}")
                },
                enabled = !gameOver
            ) {
                Text("Draw")
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        game.dealerTurn()
                        gameOver = true
                        winner = game.determineWinner()
                        if (winner == 1) {
                            message = "Gagné ! Tu as battu le méchant croupier."
                            game.handleWin(2)                        } else if (winner == 2) {
                            message = "Perdu ! Le méchant croupier vole ton argent"
                        } else {
                            message = "Egalité !"
                            game.handleDraw()
                        }
                    }
                },
                enabled = !gameOver
            ) {
                Text("Stand")
            }

        }

        if (message.isNotEmpty() && game.manche > 1) {
            Text("Résultat : $message", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Row {
                Button(
                    onClick = {
                        val betOk = game.placeBet(50)
                        if (betOk) {
                            gameOver = false
                            message = ""
                            game.startGame()
                            if (selected21plus3) {
                                game.player.money -= game.mise
                            }
                        } else {
                            message = "Mise invalide"
                        }
                    }
                ) {
                    Text("Jouer 50")
                }
                Button(
                    onClick = {
                        val betOk = game.placeBet(100)
                        if (betOk) {
                            gameOver = false
                            message = ""
                            game.startGame()
                            if (selected21plus3) {
                                game.player.money -= game.mise
                            }
                        } else {
                            message = "Mise invalide"
                        }
                    }
                ) {
                    Text("Jouer 100")
                }
                Button(
                    onClick = {
                        val betOk = game.placeBet(200)
                        if (betOk) {
                            gameOver = false
                            message = ""
                            game.startGame()
                            if (selected21plus3) {
                                game.player.money -= game.mise
                            }
                        } else {
                            message = "Mise invalide"
                        }
                    }
                ) {
                    Text("Jouer 200")
                }
            }

            Button(
                onClick = {
                    selected21plus3 = !selected21plus3
                },
                colors = if (selected21plus3) {
                    ButtonDefaults.buttonColors(containerColor = Color.Red)
                } else {
                    ButtonDefaults.buttonColors(containerColor = Color.Green)
                }
            ) {
                Text("Mise 21+3")
            }
            if (selected21plus3) {
                Text("Mise 21+3 active")
            } else {
                Text("Mise 21+3 inactive")
            }
        }
    }
}


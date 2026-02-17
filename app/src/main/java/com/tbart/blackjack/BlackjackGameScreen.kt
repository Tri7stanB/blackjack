package com.tbart.blackjack

import android.R
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tbart.blackjack.game.BlackjackGame
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BlackjackGameScreen(navController: NavHostController, gameViewModel: BlackjackViewModel) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // C'est ici que vous dessinez le contenu de votre menu
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Jouer", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.GameScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Historique", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.HistoryScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Profil", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.ProfileScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Button(
                    onClick = { scope.launch { drawerState.open() }},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text("≡", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { innerPadding ->
            // ✅ UN SEUL appel à BlackjackContent
            BlackjackContent(
                modifier = Modifier.padding(innerPadding),
                viewModel = gameViewModel
            )
        }
    }
}


@Composable
fun BlackjackContent(modifier: Modifier = Modifier, viewModel: BlackjackViewModel) {

    Log.d("BLACKJACK_DEBUG", "🎨 BlackjackContent appelé - waitingForBet=${viewModel.waitingForBet}")

    val game = viewModel.game
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Log.d("BLACKJACK_DEBUG", "⚡ LaunchedEffect exécuté")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B6623))
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(5.dp))

        Row() {
            Spacer(modifier = Modifier.weight(1f))

            Box (
                modifier = Modifier
                    .background(Color(0xFF333333), shape = RoundedCornerShape(16.dp))
                    .padding(8.dp)
            )
            {
                Text("Argent : ${game.player.money}", color = Color.Yellow, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

        }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Log.d("BLACKJACK_DEBUG", "🔀 Vérification if - waitingForBet=${viewModel.waitingForBet}")

        if (viewModel.waitingForBet) {
            Log.d("BLACKJACK_DEBUG", "✅ Branche waitingForBet=true")

            // 🎰 MODE : En attente de mise
            Text(
                "💰 Placez votre mise pour commencer",
                fontSize = 20.sp,
                color = Color.Yellow,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Afficher seulement les boutons de mise
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                listOf(50, 100, 200).forEach { mise ->
                    Button(
                        onClick = {
                            if (game.placeBet(mise)) {
                                Log.d("BLACKJACK_DEBUG", "✅ Mise acceptée")
                                viewModel.waitingForBet = false  // ← Change l'état !
                                viewModel.gameOver = false
                                viewModel.message = ""
                                Log.d("BLACKJACK_DEBUG", "🎮 Appel startGame()")
                                game.startGame()  // ← MAINTENANT on démarre
                                Log.d("BLACKJACK_DEBUG", "🎮 Partie démarrée")

                            } else {
                                Log.d("BLACKJACK_DEBUG", "❌ Mise refusée")
                                viewModel.message = "Mise invalide"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Text("Miser $mise", color = Color.White)
                    }
                }
            }

        } else {
        // --- Zone Croupier ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🤵 Croupier", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row {
                game.getDealerCards().forEach { card ->
                    CardImage(rank = card.rank.toString(), suit = card.suit.toString())
                }
            }
            Text("Score: ${game.dealerScore}", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Zone Joueur ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🧑 Joueur", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Row (    modifier = Modifier.padding(start = 16.dp)
            ){
                game.getPlayerCards().forEach { card ->
                    CardImage(rank = card.rank.toString(), suit = card.suit.toString())
                }
            }
            Text("Score: ${game.playerScore}", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Boutons Actions ---
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Button(
                onClick = {
                    game.playerHits()
                    if (game.isPlayerBusted()) {
                        viewModel.gameOver = true
                        viewModel.winner = game.determineWinner()
                        viewModel.message = if (viewModel.winner == 1) {
                            game.handleWin(2)
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "🎉 Gagné !"
                        } else if (viewModel.winner == 2) {
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "💀 Perdu !"
                        } else {
                            game.handleDraw()
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "🤝 Égalité !"
                        }
                    }
                },
                enabled = !viewModel.gameOver,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) {
                Text("Tirer", color = Color.White)
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        game.dealerTurn()
                        viewModel.gameOver = true
                        viewModel.winner = game.determineWinner()
                        viewModel.message = if (viewModel.winner == 1) {
                            game.handleWin(2)
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "🎉 Gagné !"
                        } else if (viewModel.winner == 2) {
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "💀 Perdu !"
                        } else {
                            game.handleDraw()
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "🤝 Égalité !"
                        }
                    }
                },
                enabled = !viewModel.gameOver,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Rester", color = Color.White)
            }
        }

        // --- Zone Résultat & Relance ---
        if (viewModel.message.isNotEmpty() && game.manche > 1) {
            Text("Résultat : ${viewModel.message}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.align(Alignment.CenterHorizontally)) {
                listOf(50, 100, 200).forEach { mise ->
                    Button(
                        onClick = {
                            if (game.placeBet(mise)) {
                                viewModel.gameOver = false
                                viewModel.message = ""
                                game.startGame()
                                if (viewModel.selected21plus3) game.player.money -= game.mise
                                viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            } else {
                                viewModel.message = "Mise invalide"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                    ) {
                        Text("Miser $mise", color = Color.White)
                    }
                }
            }

//            Button(
//                onClick = { viewModel.selected21plus3 = !viewModel.selected21plus3 },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = if (viewModel.selected21plus3) Color.Green else Color.Red
//                ),
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            ) {
//                Text("Mise 21+3", color = Color.White)
//            }
//
//            Text(
//                if (viewModel.selected21plus3) "Mise 21+3 active" else "Mise 21+3 inactive",
//                color = Color.White
//            )
        }
    }}
}

@Composable
fun CardImage(rank: String, suit: String) {
    val context = LocalContext.current
    val resourceName = "${rank.lowercase()}_${suit.lowercase()}"
    val imageId = remember(resourceName) {
        context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }

    if (imageId != 0) {
        Image(
            painter = painterResource(id = imageId),

            contentDescription = "$rank of $suit",
            modifier = Modifier
                .padding(4.dp)
                .size(width = 60.dp, height = 90.dp)
        )
    } else {
        Text("$rank $suit", color = Color.White) // fallback
    }
}

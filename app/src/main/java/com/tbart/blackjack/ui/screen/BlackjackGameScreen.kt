package com.tbart.blackjack.ui.screen

import android.R
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tbart.blackjack.data.manager.BlackjackManager
import com.tbart.blackjack.data.manager.UserManager
import com.tbart.blackjack.viewmodel.BlackjackViewModel
import com.tbart.blackjack.ui.navigation.Screen
import com.tbart.blackjack.ui.component.CardImage

@RequiresApi(Build.VERSION_CODES.O)
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
                    label = { Text("Amis", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.FriendScreen.route) // 2. ON NAVIGUE !
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BlackjackContent(modifier: Modifier = Modifier, viewModel: BlackjackViewModel) {

    Log.d("BLACKJACK_DEBUG", "🎨 BlackjackContent appelé - waitingForBet=${viewModel.waitingForBet}")

    val game = viewModel.game
    val coroutineScope = rememberCoroutineScope()

    val playerScrollState = rememberScrollState()
    val dealerScrollState = rememberScrollState()

    val blackjackManager = remember { BlackjackManager() }
    // Collecte la valeur du StateFlow en temps réel → recompose quand ça change
    val currentMoney by blackjackManager.money.collectAsState()

    // Synchroniser l'argent du joueur AVANT le début de la partie
    if (viewModel.waitingForBet) {
        game.player.money = currentMoney
    }

    LaunchedEffect(playerScrollState.maxValue) {
        if (playerScrollState.maxValue > 0) {
            playerScrollState.animateScrollTo(playerScrollState.maxValue)
        }
    }

    LaunchedEffect(dealerScrollState.maxValue) {
        if (dealerScrollState.maxValue > 0) {
            dealerScrollState.animateScrollTo(dealerScrollState.maxValue)
        }
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

            Card (
                modifier = Modifier.padding(10.dp),
                shape = RoundedCornerShape(1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0B6623)
                ),
                border = BorderStroke(1.dp, Color.White)
            )
            {
                Row(
                    modifier = Modifier
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    // Affiche currentMoney (état Compose) avant la partie, game.player.money pendant la partie
                    val displayMoney = if (viewModel.waitingForBet) currentMoney else game.player.money
                    Text("Argent : $displayMoney", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)

                }
        }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Log.d("BLACKJACK_DEBUG", "🔀 Vérification if - waitingForBet=${viewModel.waitingForBet}")

        if (viewModel.waitingForBet) {
            Log.d("BLACKJACK_DEBUG", "✅ Branche waitingForBet=true")

            // 🎰 MODE : En attente de mise
            Text(
                "Placez votre mise pour commencer",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Afficher seulement les boutons de mise
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
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
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)
                        ),
                        border = BorderStroke(1.dp, Color.White),
                    ) {
                        Text("Miser $mise", color = Color.White)
                    }
                }
            }

        } else {
        // --- Zone Croupier ---
        Column() {
            Text("Croupier", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 10.dp))
            Spacer(Modifier.padding(5.dp))
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .horizontalScroll(dealerScrollState)
            ) {
                game.getDealerCards().forEach { card ->
                    CardImage(rank = card.rank.toString(), suit = card.suit.toString())
                }
            }
            Spacer(Modifier.padding(5.dp))
            Text("Score: ${game.dealerScore}", color = Color.White, modifier = Modifier.padding(start = 10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Zone Joueur ---
        Column() {
            Text("Joueur", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(start = 10.dp))
            Spacer(Modifier.padding(5.dp))
            Row (
                modifier = Modifier
                    .padding(start = 10.dp)
                    .horizontalScroll(playerScrollState)
            ){
                game.getPlayerCards().forEach { card ->
                    CardImage(rank = card.rank.toString(), suit = card.suit.toString())
                }
            }
            Spacer(Modifier.padding(5.dp))
            Text("Score: ${game.playerScore}", color = Color.White, modifier = Modifier.padding(start = 10.dp))
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
                            "Gagné !"
                        } else if (viewModel.winner == 2) {
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "Perdu !"
                        } else {
                            game.handleDraw()
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "Égalité !"
                        }
                    }
                },
                enabled = !viewModel.gameOver,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF0B6623)
                ),
                border = BorderStroke(1.dp, Color.White),            ) {
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
                            "Gagné !"
                        } else if (viewModel.winner == 2) {
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "Perdu !"
                        } else {
                            game.handleDraw()
                            viewModel.updateMoney(game.player.money) // SAUVEGARDER ICI
                            "Égalité !"
                        }
                    }
                },
                enabled = !viewModel.gameOver,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF0B6623)
                ),
                border = BorderStroke(1.dp, Color.White),
                ) {
                Text("Rester", color = Color.White)
            }
        }

        // --- Zone Résultat & Relance ---
        if (viewModel.message.isNotEmpty() && game.manche > 1) {
            Text("Résultat : ${viewModel.message}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
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
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)
                        ),
                        border = BorderStroke(1.dp, Color.White),                    ) {
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

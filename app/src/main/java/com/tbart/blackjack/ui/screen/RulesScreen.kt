package com.tbart.blackjack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tbart.blackjack.ui.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun RulesScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(180.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Jouer", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.GameScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Règles", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.RulesScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Historique", fontSize = 24.sp) },
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
                    label = { Text("Amis", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.FriendScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Profil", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.ProfileScreen.route)
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF0B6623),
            topBar = {
                Button(
                    onClick = { scope.launch { drawerState.open() } },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Text("≡", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                        .padding(4.dp) // espace entre la border et le bouton
                ) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                navController.navigate(Screen.GameScreen.route)
                            }
                        },
                        contentColor = Color.White,
                        containerColor = Color(0xFF0B6623),
                        modifier = Modifier.padding(),
                        shape = FloatingActionButtonDefaults.smallShape
                    ) {
                        Text(
                            "  Je veux jouer !  "
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B6623))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .padding(innerPadding),
            ) {
                Text(
                    "Présentation du jeu",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.padding(16.dp))
                Text(
                    text = buildAnnotatedString {

                        // L'objectif
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        ) {
                            append("L'objectif\n")
                        }
                        append("Battre le croupier en obtenant une main dont la valeur est la plus proche possible de 21, sans jamais la dépasser. Si tu dépasses 21, c'est le ")
                        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                            append("bust")
                        }
                        append(" — tu perds immédiatement.\n\n")

                        // La valeur des cartes
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        ) {
                            append("La valeur des cartes\n")
                        }
                        append("• Les cartes numériques (2 à 10) valent leur valeur nominale.\n")
                        append("• Les figures (Valet, Dame, Roi) valent 10 points.\n")
                        append("• L'As vaut 1 ou 11 points, selon ce qui t'avantage le plus.\n\n")

                        // Le déroulement
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        ) {
                            append("Le déroulement d'une partie\n")
                        }
                        append("1. Tu places ta mise avant que les cartes soient distribuées.\n")
                        append("2. Tu reçois deux cartes visibles. Le croupier reçoit deux cartes, dont une seule est visible.\n")
                        append("3. Tu choisis ensuite ton action :\n")

                        append("   • ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Tirer (Hit)") }
                        append(" : recevoir une carte supplémentaire.\n")

                        append("   • ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Rester (Stand)") }
                        append(" : garder ta main telle quelle.\n")

                        append("4. Une fois que tu as terminé, le croupier révèle sa carte cachée et tire des cartes jusqu'à atteindre au moins 17 points.\n\n")

                        // Qui gagne
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        ) {
                            append("Qui gagne ?\n")
                        }
                        append("• Ta main est plus proche de 21 que celle du croupier → ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Tu gagnes !\n") }

                        append("• Le croupier dépasse 21 → ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Tu gagnes !\n") }

                        append("• Égalité → ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Mise remboursée.\n") }

                        append("• Le croupier a une meilleure main ou tu dépasses 21 → ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Tu perds.") }
                    },
                    color = Color.White
                )
            }
        }
    }
}
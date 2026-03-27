package com.tbart.blackjack.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.tbart.blackjack.ui.component.DailyRecordItem
import com.tbart.blackjack.ui.navigation.Screen
import com.tbart.blackjack.viewmodel.BlackjackViewModel
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(navController: NavHostController, viewModel: BlackjackViewModel) {
    val history = viewModel.getDailyHistory()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.checkForNewDay()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // C'est ici que vous dessinez le contenu de votre menu
            ModalDrawerSheet(
                modifier = Modifier.width(180.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Jouer", fontSize = 24.sp) },
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
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.FriendScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Profil", fontSize = 24.sp) },
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
        // Le contenu principal de votre jeu
        Scaffold(
            topBar = {
                // Optionnel : Une petite barre pour ouvrir le menu
                Button(
                    onClick = { scope.launch { drawerState.open() } },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )

                ) {
                    Text("≡", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { innerPadding ->


            val history = viewModel.getDailyHistory()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B6623))
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                // En-tête
                Text(
                    "Historique des gains",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Liste des records
                if (history.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Aucun historique pour le moment",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(history) { record ->
                            DailyRecordItem(record)
                        }
                    }
                }
            }
        }
    }
}

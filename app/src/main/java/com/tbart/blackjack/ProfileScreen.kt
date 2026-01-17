package com.tbart.blackjack

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // C'est ici que vous dessinez le contenu de votre menu
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                NavigationDrawerItem(
                    label = { Text("Menu", fontSize = 20.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.MenuScreen.route)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Jouer", fontSize = 20.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.GameScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Historique", fontSize = 20.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.HistoryScreen.route)
                        }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Profil", fontSize = 20.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.ProfileScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
                // Ajoutez d'autres items ici...
            }
        }
    ) {
        // Le contenu principal de votre jeu
        Scaffold(
            topBar = {
                // Optionnel : Une petite barre pour ouvrir le menu
                Button(
                    onClick = { scope.launch { drawerState.open() }},
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )

                ) {
                    Text("≡", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { innerPadding ->
            // On appelle votre interface actuelle ici
            Text(
                text = "Écran de Profil",
                modifier = Modifier.padding(innerPadding).padding(16.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
package com.tbart.blackjack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val sharedViewModel: BlackjackViewModel = viewModel()

    NavHost(navController = navController, startDestination = Screen.MenuScreen.route) {
        composable(route = Screen.MenuScreen.route) {
            MenuScreen(navController)
        }
        composable(route = Screen.GameScreen.route) {
            BlackjackGameScreen(navController, sharedViewModel)
        }
        composable(route = Screen.ProfileScreen.route){
            ProfileScreen(navController)
        }
        composable(route = Screen.HistoryScreen.route){
            HistoryScreen(navController, sharedViewModel)
        }
        composable(route = Screen.FriendScreen.route){
            FriendScreen(navController)
        }

    }
}
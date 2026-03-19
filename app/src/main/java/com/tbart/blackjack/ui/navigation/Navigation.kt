package com.tbart.blackjack.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tbart.blackjack.viewmodel.BlackjackViewModel
import com.tbart.blackjack.ui.screen.MenuScreen
import com.tbart.blackjack.ui.screen.BlackjackGameScreen
import com.tbart.blackjack.ui.screen.ProfileScreen
import com.tbart.blackjack.ui.screen.HistoryScreen
import com.tbart.blackjack.ui.screen.FriendScreen

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

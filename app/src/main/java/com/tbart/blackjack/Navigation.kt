package com.tbart.blackjack

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.GameScreen.route) {
        composable(route = Screen.GameScreen.route) {
            BlackjackGameScreen(navController)
        }
        composable(route = Screen.ProfileScreen.route){
            ProfileScreen(navController)
        }
    }
}
package com.tbart.blackjack

sealed class Screen(val route : String) {
    object SplashScreen : Screen("splashScreen")
    object MainMenuScreen : Screen("mainMenuScreen")
    object GameScreen : Screen("gameScreen")
    object ProfileScreen : Screen("profileScreen")
}
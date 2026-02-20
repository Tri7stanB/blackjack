package com.tbart.blackjack

sealed class Screen(val route : String) {
    object SplashScreen : Screen("splashScreen")
    object MenuScreen : Screen("menuScreen")
    object GameScreen : Screen("gameScreen")
    object ProfileScreen : Screen("profileScreen")
    object FriendScreen : Screen("friendScreen")
    object HistoryScreen : Screen("historyScreen")
}
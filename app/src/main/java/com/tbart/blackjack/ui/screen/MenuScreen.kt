package com.tbart.blackjack.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.tbart.blackjack.data.manager.BlackjackManager
import com.tbart.blackjack.ui.navigation.Screen

@Composable
fun MenuScreen(navController: androidx.navigation.NavHostController) {
    val blackjackManager = remember { BlackjackManager() }
    val hasSeenRules by blackjackManager.hasSeenRules.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Button(
            onClick = {
                if (!hasSeenRules) {
                    navController.navigate(Screen.RulesScreen.route)
                } else {
                    navController.navigate(Screen.GameScreen.route)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B6623)
            )
        ) {
            Text("Jouer", fontSize = 48.sp)
        }
    }
}

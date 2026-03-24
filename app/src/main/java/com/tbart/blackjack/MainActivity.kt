package com.tbart.blackjack

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.tbart.blackjack.data.worker.DailyReminderWorker
import com.tbart.blackjack.ui.theme.BlackJackTheme
import com.tbart.blackjack.ui.navigation.Navigation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enregistrer l'ouverture de l'app
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        getSharedPreferences(DailyReminderWorker.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(DailyReminderWorker.KEY_LAST_OPEN_DATE, today)
            .apply()

// 1. On active le mode plein écran
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, window.decorView)
        // 2. On masque les barres (Status bars = haut, Navigation = bas)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        // 3. On permet de les faire réapparaître d'un simple "swipe" sans décaler le layout
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            Navigation()
        }
    }
}

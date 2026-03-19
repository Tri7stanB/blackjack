package com.tbart.blackjack.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun CardImage(rank: String, suit: String) {
    val context = LocalContext.current
    val resourceName = "${rank.lowercase()}_${suit.lowercase()}"
    val imageId = remember(resourceName) {
        context.resources.getIdentifier(resourceName, "drawable", context.packageName)
    }

    if (imageId != 0) {
        Image(
            painter = painterResource(id = imageId),

            contentDescription = "$rank of $suit",
            modifier = Modifier
                .padding(4.dp)
                .size(width = 60.dp, height = 90.dp)
        )
    } else {
        Text("$rank $suit", color = Color.White) // fallback
    }
}

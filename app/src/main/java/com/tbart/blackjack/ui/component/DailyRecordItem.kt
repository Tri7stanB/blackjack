package com.tbart.blackjack.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tbart.blackjack.data.model.DailyRecord
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DailyRecordItem(record: DailyRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B6623)
        ),
        border = BorderStroke(1.dp, Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = try {
                    val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(record.date)
                    SimpleDateFormat("EEE dd MMM yyyy", Locale.FRENCH).format(parsed!!)
                } catch (e: Exception) {
                    record.date
                },
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = if (record.money >= 1000) "+${record.money-1000}$" else "${record.money-1000}$",
                color = if (record.money >= 1000) Color.Green else Color.Red,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

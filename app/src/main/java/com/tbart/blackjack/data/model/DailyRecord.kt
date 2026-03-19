package com.tbart.blackjack.data.model

data class DailyRecord(
    val date: String,
    val money: Int // Gains du jour (peut être négatif)
)

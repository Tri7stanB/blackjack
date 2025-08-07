package com.tbart.blackjack.model

data class Player(val name: String, var money: Int, val hand: Hand = Hand())

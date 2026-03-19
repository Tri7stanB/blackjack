package com.tbart.blackjack.data.manager

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BlackjackManager {
    val db = Firebase.firestore

    private val _money = MutableStateFlow(1000)
    val money: StateFlow<Int> = _money.asStateFlow()

    init {
        loadMoney()
    }

    private fun loadMoney() {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val currentMoney = snapshot.getLong("currentMoney")?.toInt() ?: 1000
                _money.value = currentMoney
            }
    }
}
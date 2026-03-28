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

    private val _hasSeenRules = MutableStateFlow(true) // true par défaut pour éviter un flash de l'écran règles
    val hasSeenRules: StateFlow<Boolean> = _hasSeenRules.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val currentMoney = snapshot.getLong("currentMoney")?.toInt() ?: 1000
                _money.value = currentMoney

                // Si le champ n'existe pas encore → c'est un nouveau joueur → false
                val seenRules = snapshot.getBoolean("hasSeenRules") ?: false
                _hasSeenRules.value = seenRules
            }
    }

    fun markRulesAsSeen() {
        _hasSeenRules.value = true
        val userId = Firebase.auth.currentUser?.uid ?: return
        db.collection("users")
            .document(userId)
            .update("hasSeenRules", true)
            .addOnFailureListener { e ->
                Log.e("BlackjackManager", "Erreur sauvegarde hasSeenRules", e)
            }
    }
}
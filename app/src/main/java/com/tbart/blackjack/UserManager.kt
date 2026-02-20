package com.tbart.blackjack

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserManager {
    val firestore = FirebaseFirestore.getInstance()

    // Récupère le code ami depuis Firestore
    fun getPlayerId(onResult: (String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onResult(null)
            return
        }

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val id = document.getString("playerId")
                onResult(id)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun createUniquePlayerId(onComplete: (String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        fun tryGenerate() {
            val newId = generatePlayerId()

            firestore.collection("playerIds")
                .document(newId)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {

                        // 🔥 ID libre → on le réserve
                        firestore.collection("playerIds")
                            .document(newId)
                            .set(mapOf("uid" to uid))

                        firestore.collection("users")
                            .document(uid)
                            .set(
                                mapOf("playerId" to newId),
                                com.google.firebase.firestore.SetOptions.merge()
                            )

                        // Écrire aussi dans le profil public
                        firestore.collection("users")
                            .document(uid)
                            .collection("public")
                            .document("profile")
                            .set(
                                mapOf("playerId" to newId),
                                com.google.firebase.firestore.SetOptions.merge()
                            )

                        onComplete(newId)
                    } else {
                        // ID déjà pris → on réessaie
                        tryGenerate()
                    }
                }
        }

        tryGenerate()
    }

    private fun generatePlayerId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }
    // Met à jour le profil public (lisible par les autres utilisateurs)
    fun updatePublicProfile(data: Map<String, Any>) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        firestore.collection("users")
            .document(uid)
            .collection("public")
            .document("profile")
            .set(data, com.google.firebase.firestore.SetOptions.merge())
    }

}

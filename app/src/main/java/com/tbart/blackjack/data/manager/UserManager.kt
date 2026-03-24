package com.tbart.blackjack.data.manager

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class UserManager {
    val db = Firebase.firestore
    var money: Int? = 1000

    fun loadMoney(){
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                // Les UIDs stockés dans Firestore
                val playerMoney = (snapshot.get("currentMoney") as? Int)
                money = playerMoney
            }

    }


    // Récupère le code ami depuis Firestore
    fun getPlayerId(onResult: (String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onResult(null)
            return
        }

        db.collection("users")
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

    fun getUsername(onResult: (String?) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            onResult("")
            return
        }
            db.collection("users")
                .document(uid)
                .collection("public")
                .document("profile")
                .get()
                .addOnSuccessListener { document ->
                    val id: String? = document.getString("username")
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

            db.collection("playerIds")
                .document(newId)
                .get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {

                        // 🔥 ID libre → on le réserve
                        db.collection("playerIds")
                            .document(newId)
                            .set(mapOf("uid" to uid))

                        db.collection("users")
                            .document(uid)
                            .set(
                                mapOf("playerId" to newId),
                                com.google.firebase.firestore.SetOptions.merge()
                            )

                        db.collection("users")
                            .document(uid)
                            .set(
                                mapOf("currentMoney" to 1000),
                                com.google.firebase.firestore.SetOptions.merge()
                            )

                        // Écrire aussi dans le profil public
                        db.collection("users")
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
        db.collection("users")
            .document(uid)
            .collection("public")
            .document("profile")
            .set(data, com.google.firebase.firestore.SetOptions.merge())
    }

    fun deleteAccount(onComplete: () -> Unit = {}, onError: (Exception) -> Unit = {}) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        getPlayerId { playerId ->

            // 1. Supprimer dailyGains
            db.collection("users").document(uid)
                .collection("dailyGains").get()
                .addOnSuccessListener { snapshot ->
                    val tasks = snapshot.documents.map { it.reference.delete() }
                    Tasks.whenAll(tasks)
                        .addOnCompleteListener { // addOnComplete = peu importe si ça échoue, on continue
                            // 2. Supprimer public/profile
                            db.collection("users").document(uid)
                                .collection("public").document("profile").delete()
                                .addOnCompleteListener {
                                    // 3. Supprimer playerIds
                                    val playerTask = if (!playerId.isNullOrEmpty())
                                        db.collection("playerIds").document(playerId).delete()
                                    else Tasks.forResult(null)

                                    playerTask.addOnCompleteListener {
                                        // 4. Supprimer le document user principal
                                        db.collection("users").document(uid).delete()
                                            .addOnCompleteListener {
                                                // 5. Supprimer le compte Auth EN DERNIER
                                                user.delete()
                                                    .addOnSuccessListener { onComplete() }
                                                    .addOnFailureListener { exception -> onError(exception) }
                                            }
                                    }
                                }
                        }
                }
                .addOnFailureListener { exception -> onError(exception) }
        }
    }



}

package com.tbart.blackjack.data.manager

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.tbart.blackjack.data.model.FriendItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FriendManager {
    val db = Firebase.firestore
    private val _friends = MutableStateFlow<List<FriendItem>>(emptyList())
    val friends: StateFlow<List<FriendItem>> = _friends.asStateFlow()

    init {
        listenToFriends()
    }

    private fun listenToFriends() {
        val userId = Firebase.auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                // Les UIDs stockés dans Firestore
                val friendUids = (snapshot.get("friends") as? List<*>)
                    ?.filterIsInstance<String>() ?: emptyList()

                if (friendUids.isEmpty()) {
                    _friends.value = emptyList()
                    return@addSnapshotListener
                }

                val result = mutableListOf<FriendItem>()
                var remaining = friendUids.size

                for (uid in friendUids) {
                    db.collection("users")
                        .document(uid)
                        .collection("public")
                        .document("profile")
                        .get()
                        .addOnSuccessListener { profileDoc ->
                            val playerId = profileDoc.getString("playerId") ?: "???"
                            val username = profileDoc.getString("username") ?: "Inconnu"
                            result.add(FriendItem(playerId = playerId, username = username))
                            remaining--
                            if (remaining == 0) {
                                _friends.value = result  // ✅ mise à jour du StateFlow
                            }
                        }
                        .addOnFailureListener {
                            remaining--
                            if (remaining == 0) {
                                _friends.value = result
                            }
                        }
                }
            }
    }

    fun getFriends(callback: (List<FriendItem>) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return callback(emptyList())

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val rawFriends = document.get("friends")
                Log.d("FriendManager", "Raw friends field: $rawFriends (type: ${rawFriends?.javaClass})")

                val friendUids = (rawFriends as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                Log.d("FriendManager", "Friend UIDs: $friendUids")

                if (friendUids.isEmpty()) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                val friends = mutableListOf<FriendItem>()
                var remaining = friendUids.size

                for (uid in friendUids) {
                    db.collection("users")
                        .document(uid)
                        .collection("public")
                        .document("profile")
                        .get()
                        .addOnSuccessListener { friendDoc ->
                            Log.d("FriendManager", "Fetched public profile for $uid, exists=${friendDoc.exists()}, data=${friendDoc.data}")
                            val playerId = friendDoc.getString("playerId") ?: "???"
                            val username = friendDoc.getString("username") ?: "Inconnu"
                            friends.add(FriendItem(playerId = playerId, username = username))
                            remaining--
                            if (remaining == 0) {
                                callback(friends)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FriendManager", "Failed to fetch public profile for $uid", e)
                            remaining--
                            if (remaining == 0) {
                                callback(friends)
                            }
                        }
                }
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun searchFriend(playerId: String, callback: (FriendItem?) -> Unit) {

        // 1️⃣ Chercher dans playerIds
        db.collection("playerIds")
            .document(playerId)
            .get()
            .addOnSuccessListener { playerDoc ->

                if (!playerDoc.exists()) {
                    callback(null)
                    return@addOnSuccessListener
                }

                val uid = playerDoc.getString("uid")
                if (uid == null) {
                    callback(null)
                    return@addOnSuccessListener
                }

                // 2️⃣ Aller chercher son profil public
                db.collection("users")
                    .document(uid)
                    .collection("public")
                    .document("profile")
                    .get()
                    .addOnSuccessListener { profileDoc ->

                        if (!profileDoc.exists()) {
                            callback(null)
                            return@addOnSuccessListener
                        }

                        val username = profileDoc.getString("username") ?: "Inconnu"

                        val friend = FriendItem(
                            playerId = playerId,
                            username = username
                        )

                        callback(friend)
                    }
                    .addOnFailureListener {
                        callback(null)
                    }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun addFriend(friend: FriendItem) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val friendId = friend.playerId

        db.collection("playerIds")
            .document(friendId)
            .get()
            .addOnSuccessListener { friendDoc ->
                val friendUid = friendDoc.getString("uid") ?: return@addOnSuccessListener

                // ✅ Ici on est sûr que friendUid est disponible
                db.collection("users")
                    .document(userId)
                    .update("friends", FieldValue.arrayUnion(friendUid))
                    .addOnSuccessListener {
                        Log.d("Firestore", "Ami ajouté avec succès")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erreur lors de l'ajout", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Joueur introuvable", e)
            }
    }

    fun deleteFriend(friend: FriendItem){
        val userId = Firebase.auth.currentUser?.uid ?: return
        val friendId = friend.playerId

        db.collection("playerIds")
            .document(friendId)
            .get()
            .addOnSuccessListener { friendDoc ->
                val friendUid = friendDoc.getString("uid") ?: return@addOnSuccessListener

                // ✅ Ici on est sûr que friendUid est disponible
                db.collection("users")
                    .document(userId)
                    .update("friends", FieldValue.arrayRemove(friendUid))
                    .addOnSuccessListener {
                        Log.d("Firestore", "Ami supprimé avec succès")
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erreur lors de la suppression", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Joueur introuvable", e)
            }
    }

    fun sendMoneyTo(friend: FriendItem, amount: Int, onResult: (success: Boolean, error: String?) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return
        val friendId = friend.playerId

        db.collection("playerIds")
            .document(friendId)
            .get()
            .addOnSuccessListener { friendDoc ->
                val friendUid = friendDoc.getString("uid") ?: return@addOnSuccessListener

                val myRef = db.collection("users").document(userId)
                val friendRef = db.collection("users").document(friendUid)

                db.runTransaction { transaction ->
                    val myDoc = transaction.get(myRef)
                    val myMoney = myDoc.getLong("currentMoney") ?: 0

                    if (myMoney < amount) throw Exception("Solde insuffisant")

                    transaction.update(myRef, "currentMoney", myMoney - amount)
                    transaction.update(friendRef, "currentMoney", FieldValue.increment(amount.toLong()))
                }
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { e -> onResult(false, e.message) }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Joueur introuvable", e)
            }
    }
}

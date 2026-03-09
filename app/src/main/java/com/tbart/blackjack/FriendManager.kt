package com.tbart.blackjack

import android.util.Log
import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

data class FriendItem(
    val playerId: String,
    val username: String
)

class FriendManager {
    fun getFriends(callback: (List<FriendItem>) -> Unit) {
        val userId = Firebase.auth.currentUser?.uid ?: return callback(emptyList())

        Firebase.firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val rawFriends = document.get("friends")
                android.util.Log.d("FriendManager", "Raw friends field: $rawFriends (type: ${rawFriends?.javaClass})")

                val friendUids = (rawFriends as? List<*>)?.filterIsInstance<String>() ?: emptyList()
                android.util.Log.d("FriendManager", "Friend UIDs: $friendUids")

                if (friendUids.isEmpty()) {
                    callback(emptyList())
                    return@addOnSuccessListener
                }

                val friends = mutableListOf<FriendItem>()
                var remaining = friendUids.size

                for (uid in friendUids) {
                    Firebase.firestore.collection("users")
                        .document(uid)
                        .collection("public")
                        .document("profile")
                        .get()
                        .addOnSuccessListener { friendDoc ->
                            android.util.Log.d("FriendManager", "Fetched public profile for $uid, exists=${friendDoc.exists()}, data=${friendDoc.data}")
                            val playerId = friendDoc.getString("playerId") ?: "???"
                            val username = friendDoc.getString("username") ?: "Inconnu"
                            friends.add(FriendItem(playerId = playerId, username = username))
                            remaining--
                            if (remaining == 0) {
                                callback(friends)
                            }
                        }
                        .addOnFailureListener { e ->
                            android.util.Log.e("FriendManager", "Failed to fetch public profile for $uid", e)
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

        val db = Firebase.firestore

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

        Firebase.firestore.collection("playerIds")
            .document(friendId)
            .get()
            .addOnSuccessListener { friendDoc ->
                val friendUid = friendDoc.getString("uid") ?: return@addOnSuccessListener

                // ✅ Ici on est sûr que friendUid est disponible
                Firebase.firestore.collection("users")
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
}
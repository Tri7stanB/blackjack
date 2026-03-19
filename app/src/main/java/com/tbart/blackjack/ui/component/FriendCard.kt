package com.tbart.blackjack.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tbart.blackjack.data.manager.FriendManager
import com.tbart.blackjack.data.model.FriendItem

@Composable
fun FriendCard(friend : FriendItem, friendButton : Boolean = false, himself : Boolean = false, friendManager: FriendManager = FriendManager()){
    var showTransferDialog by remember { mutableStateOf(false) }

    if (showTransferDialog) {
        TransferDialog(
            friend = friend,
            onDismiss = { showTransferDialog = false },
            friendManager = friendManager
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0B6623),
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(friend.playerId)
            Spacer(modifier = Modifier.width(20.dp))
            Text(friend.username)
            Spacer(modifier = Modifier.weight(1f))
            if (!himself){
                if (friendButton){
                    Button(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)),
                        border = BorderStroke(1.dp, Color.White),
                        onClick = {
                            friendManager.addFriend(friend)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Ajouter un ami",
                        )
                    }
                }
                else {
                    Button(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)),
                        border = BorderStroke(1.dp, Color.White),
                        onClick = {
                            showTransferDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = "Envoyer de l'argent",
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)),
                        border = BorderStroke(1.dp, Color.White),
                        onClick = {
                            friendManager.deleteFriend(friend)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Supprimer un ami",
                        )
                    }

                }
            }
            else {
                Text("Vous")
            }
        }
    }
}

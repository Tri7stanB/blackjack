package com.tbart.blackjack.ui.component

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tbart.blackjack.data.manager.FriendManager
import com.tbart.blackjack.data.model.FriendItem

@Composable
fun TransferDialog(
    friend: FriendItem,
    onDismiss: () -> Unit,
    friendManager: FriendManager
) {
    var amount by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedButton by remember { mutableStateOf("none") }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = Color(0xFF0B6623),
        title = { Text("Envoyer de l'argent", color = Color.White) },
        text = {
            Column {
                Text("Destinataire : ${friend.username}", color = Color.White)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = {
                            selectedButton = if (selectedButton=="50") { "none" } else { "50" }
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = if (selectedButton=="50") Color.Green else Color(0xFF0B6623)),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text("50")
                    }
                    Spacer(Modifier.padding(8.dp))
                    Button(
                        onClick = {
                            selectedButton = if (selectedButton=="100") { "none" } else { "100" }
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = if (selectedButton=="100") Color.Green else Color(0xFF0B6623)),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text("100")
                    }
                    Spacer(Modifier.padding(8.dp))
                    Button(
                        onClick = {
                            selectedButton = if (selectedButton=="200") { "none" } else { "200" }
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = if (selectedButton=="200") Color.Green else Color(0xFF0B6623)),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Text("200")
                    }
                }
                errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(it, color = Color.Red, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedButton=="none") {
                        return@Button
                    }
                    isLoading = true
                    friendManager.sendMoneyTo(friend, selectedButton.toInt()) { success, error ->
                        isLoading = false
                        if (success) onDismiss()
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF0B6623)),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(if (isLoading) "Envoi..." else "Confirmer")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Color(0xFF0B6623)),
                border = BorderStroke(1.dp, Color.White))
            {
                Text("Annuler")
            }
        }
    )
}

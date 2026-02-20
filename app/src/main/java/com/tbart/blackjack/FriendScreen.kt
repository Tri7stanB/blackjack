package com.tbart.blackjack

import android.R
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Locale.getDefault


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val auth = Firebase.auth
    val context = LocalContext.current
    val userManager = UserManager()
    val friendManager = FriendManager()


    // État pour le code ami (chargé de manière asynchrone)
    var friendCode by remember { mutableStateOf<String?>(null) }

    // Charger le code ami au lancement de l'écran
    LaunchedEffect(Unit) {
        userManager.getPlayerId { id ->
            friendCode = id
        }
    }

    var friends by remember { mutableStateOf(listOf<FriendItem>()) }

    LaunchedEffect(Unit) {
        friendManager.getFriends { amis ->
            friends = amis
        }

    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // C'est ici que vous dessinez le contenu de votre menu
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Jouer", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.GameScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Historique", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.HistoryScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Amis", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.FriendScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Profil", fontSize = 30.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close() // 1. On ferme le menu
                            navController.navigate(Screen.ProfileScreen.route) // 2. ON NAVIGUE !
                        }
                    }
                )
            }
        }
    ) {
        // Le contenu principal de votre jeu
        Scaffold(
            topBar = {
                // Optionnel : Une petite barre pour ouvrir le menu
                Button(
                    onClick = { scope.launch { drawerState.open() }},
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )

                ) {
                    Text("≡", color = Color.Black, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0B6623))
                    .padding(innerPadding)
                    .padding(16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.Bottom
                ){
                    var friendInput by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = friendInput.uppercase(getDefault()),
                        onValueChange = { friendInput = it },
                        label = { Text("Ajouter un ami")},
                        textStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLabelColor = Color.White,
                            cursorColor = Color.White)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {

                        },
                        modifier = Modifier.size(56.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)
                        ),
                        border = BorderStroke(1.dp, Color.White),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Rechercher un ami",
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Vos amis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                friends.forEach { friend ->
                    FriendCard(friend)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun FriendCard(friend : FriendItem){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF0B6623),
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(friend.username)
            Spacer(modifier = Modifier.width(80.dp))
            Text(friend.playerId)
        }
    }
}
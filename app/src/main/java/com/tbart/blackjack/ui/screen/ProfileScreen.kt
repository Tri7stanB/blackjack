package com.tbart.blackjack.ui.screen

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import com.tbart.blackjack.activity.ConnectionActivity
import com.tbart.blackjack.data.manager.UserManager
import com.tbart.blackjack.ui.navigation.Screen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val auth = Firebase.auth
    val context = LocalContext.current
    val userManager = UserManager()

    // État pour le code ami (chargé de manière asynchrone)
    var friendCode by remember { mutableStateOf<String?>(null) }
    var username: String? by remember { mutableStateOf("") }

    var showDialog by remember { mutableStateOf(false) }
    var showReauthDialog by remember { mutableStateOf(false) }
    var reauthPassword by remember { mutableStateOf("") }
    var reauthError by remember { mutableStateOf<String?>(null) }

    val clip: ClipData = ClipData.newPlainText("code ami", friendCode)


    // Fonction de chargement des données
    fun loadData() {
        userManager.getPlayerId { id ->
            friendCode = id
        }
        userManager.getUsername { name ->
            username = name
        }
    }

    fun textCopyThenPost(textCopied: String?) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("", textCopied))
        // Only show a toast for Android 12 and lower.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
            Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun showDialog() {
        showDialog = true
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            content = {
                var usernameTempo: String? by remember { mutableStateOf(username) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF0B6623),
                        contentColor = Color.White
                    )
                ) {
                    Column() {
                        Spacer(modifier = Modifier.padding(8.dp))
                        Row() {
                            Spacer(modifier = Modifier.padding(8.dp))
                            usernameTempo?.let {
                                TextField(
                                    value = it,
                                    onValueChange = { usernameTempo = it },
                                    label = { Text("Nouveau pseudo") },
                                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.White,
                                        unfocusedBorderColor = Color.White,
                                        unfocusedLabelColor = Color.White,
                                        focusedLabelColor = Color.White,
                                        cursorColor = Color.White
                                    ),
                                )
                            }
                            Spacer(modifier = Modifier.padding(8.dp))
                        }
                        Spacer(modifier = Modifier.padding(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = {
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color(0xFF0B6623)
                                ),
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Text(
                                    text = "Annuler",
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.padding(16.dp))
                            Button(
                                onClick = {
                                    username = usernameTempo
                                    username?.let { userManager.updatePublicProfile(mapOf("username" to it)) }
                                    showDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color(0xFF0B6623),
                                ),
                                border = BorderStroke(1.dp, Color.White)
                            ) {
                                Text(
                                    text = "Valider",
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        )
    }

    // Charger le code ami au lancement de l'écran
    LaunchedEffect(Unit) {
        loadData()
    }

    if (showDialog) {
        showDialog()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(180.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Jouer", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.GameScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Règles", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.RulesScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Historique", fontSize = 24.sp) },
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
                    label = { Text("Amis", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.FriendScreen.route)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                NavigationDrawerItem(
                    label = { Text("Profil", fontSize = 24.sp) },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                            navController.navigate(Screen.ProfileScreen.route)
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF0B6623),
            topBar = {
                Button(
                    onClick = { scope.launch { drawerState.open() } },
                    colors = ButtonDefaults.buttonColors(
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
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFF0B6623))
                    .padding(16.dp)
                    .padding(innerPadding)
            ) {
                Row() {
                    Column() {
                        Text(
                            text = "Profil",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.padding(16.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Pseudo : $username",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Button(
                                onClick = {
                                    showDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color(0xFF0B6623)
                                ),
                            )
                            {
                                Icon(
                                    imageVector = Icons.Filled.Create,
                                    contentDescription = "Modifier le pseudo",
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Email : ${auth.currentUser?.email ?: "Non connecté"}",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Code ami : ${friendCode ?: "Code ami non trouvé"}",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Button(
                                onClick = {
                                    textCopyThenPost(friendCode)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color(0xFF0B6623)
                                ),
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copier le code ami",
                                    tint = Color.White,
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = {
                            auth.signOut()
                            val intent = Intent(context, ConnectionActivity::class.java)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)
                        ),
                        border = BorderStroke(1.dp, Color.White),
                    ) {
                        Text(
                            "Se déconnecter",
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            showReauthDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Red,
                            containerColor = Color(0xFF0B6623)
                        ),
                        border = BorderStroke(1.dp, Color.Red),
                    ) {
                        Text(
                            "Supprimer mon compte",
                            color = Color.Red
                        )
                    }
                }
            }
        }
    }



    if (showReauthDialog) {
        AlertDialog(
            onDismissRequest = {
                showReauthDialog = false; reauthPassword = ""; reauthError = null
            },
            title = { Text("Es-tu sûr de vouloir supprimer ton compte ?", color = Color.White) },
            containerColor = Color(0xFF0B6623),
            text = {
                Column {
                    Text("Entre ton mot de passe pour confirmer.", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reauthPassword,
                        onValueChange = { reauthPassword = it; reauthError = null },
                        label = { Text("Mot de passe") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White,
                            unfocusedLabelColor = Color.White,
                            focusedLabelColor = Color.White,
                            cursorColor = Color.White
                        )
                    )
                    reauthError?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(it, color = Color.Red, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val user = auth.currentUser ?: return@Button
                        val email = user.email ?: return@Button
                        val credential = EmailAuthProvider.getCredential(email, reauthPassword)
                        user.reauthenticate(credential)
                            .addOnSuccessListener {
                                showReauthDialog = false
                                reauthPassword = ""
                                userManager.deleteAccount(
                                    onComplete = {
                                        auth.signOut()
                                        val intent = Intent(context, ConnectionActivity::class.java)
                                        context.startActivity(intent)
                                    }
                                )
                            }
                            .addOnFailureListener { reauthError = "Mot de passe incorrect." }
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Red,
                        containerColor = Color(0xFF0B6623)
                    ),
                    border = BorderStroke(1.dp, Color.Red),
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton =
                {
                    Button(
                        onClick = {
                            showReauthDialog = false; reauthPassword = ""; reauthError = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFF0B6623)
                        ),
                        border = BorderStroke(1.dp, Color.White),
                    ) {
                        Text("Annuler")
                    }
                }
        )
    }
}

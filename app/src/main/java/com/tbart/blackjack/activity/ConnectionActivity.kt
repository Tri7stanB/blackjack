package com.tbart.blackjack.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.tbart.blackjack.R
import com.tbart.blackjack.MainActivity
import com.tbart.blackjack.data.manager.DailyMoneyManager
import com.tbart.blackjack.data.worker.DailyReminderWorker
import com.tbart.blackjack.data.worker.NotificationScheduler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConnectionActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button
    private lateinit var dailyMoneyManager: DailyMoneyManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        // Enregistrer l'ouverture de l'app aujourd'hui
        recordAppOpen()
        // Planifier la notification quotidienne à 17h
        NotificationScheduler.scheduleDailyReminder(this)
        // Demander la permission de notifications (Android 13+)
        requestNotificationPermission()

        // 1. Vérifier si l'utilisateur est déjà connecté
        // VERIFICATION DE LA SESSION
        if (auth.currentUser != null) {
            // L'utilisateur est déjà connecté !
            // On crée le manager APRÈS auth pour avoir le bon UID
            dailyMoneyManager = DailyMoneyManager(this)
            // On synchronise puis on redirige vers le menu
            dailyMoneyManager.syncFromFirestore {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            return // On arrête l'exécution de onCreate ici (pas de setContentView)
        }

        // 2. Lier le code au fichier XML (vérifiez le nom de votre layout)
        setContentView(R.layout.activity_connection)

        // 4. Faire le lien avec les éléments du design XML
        emailField = findViewById(R.id.emailEditText)
        passwordField = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.registerButton)
        signInButton = findViewById(R.id.signInButton)


        // 5. Déclencher l'action au clic sur le bouton
        signInButton.setOnClickListener {
            signInUser()
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun recordAppOpen() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        getSharedPreferences(DailyReminderWorker.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(DailyReminderWorker.KEY_LAST_OPEN_DATE, today)
            .apply()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    100
                )
            }
        }
    }

    private fun signInUser(){
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // On crée le manager APRÈS login réussi pour avoir le bon UID
                        dailyMoneyManager = DailyMoneyManager(this)
                        dailyMoneyManager.syncFromFirestore {
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        // Échec : afficher le message d'erreur
                        Toast.makeText(baseContext, "Erreur : ${task.exception?.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

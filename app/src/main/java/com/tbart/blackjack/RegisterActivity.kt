package com.tbart.blackjack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInButton: Button
    private lateinit var dailyMoneyManager: DailyMoneyManager
    private lateinit var userManager : UserManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. Initialiser Firebase
        auth = Firebase.auth
        dailyMoneyManager = DailyMoneyManager(this)
        userManager = UserManager()


        // VERIFICATION DE LA SESSION
        if (auth.currentUser != null) { //user deja connecté
            if (auth.currentUser != null) {

                dailyMoneyManager.syncFromFirestore() // 🔥 SYNC ICI

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                return
            }

        }

        // 2. Lier le code au fichier XML (vérifiez le nom de votre layout)
        setContentView(R.layout.activity_register)

        // 4. Faire le lien avec les éléments du design XML
        emailField = findViewById(R.id.emailEditText)
        passwordField = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.registerButton)
        signInButton = findViewById(R.id.signInButton)


        // 5. Déclencher l'action au clic sur le bouton
        signUpButton.setOnClickListener {
            signUpUser()
        }

        signInButton.setOnClickListener {
            val intent = Intent(this, ConnectionActivity::class.java)
            startActivity(intent)        }
    }

    private fun signUpUser() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Créer le DailyMoneyManager après inscription (UID disponible)
                        dailyMoneyManager = DailyMoneyManager(this)

                        // Générer le code ami APRÈS inscription réussie
                        userManager.createUniquePlayerId { id ->
                            Log.d("PLAYER_ID", "ID généré : $id")
                        }

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

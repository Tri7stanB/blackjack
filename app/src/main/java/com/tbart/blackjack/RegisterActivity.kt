package com.tbart.blackjack

import android.content.Intent
import android.os.Bundle
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. Initialiser Firebase
        auth = Firebase.auth
        // VERIFICATION DE LA SESSION
        if (auth.currentUser != null) {
            // L'utilisateur est déjà connecté !
            // On le redirige immédiatement vers le jeu sans afficher l'inscription
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Très important pour qu'il ne puisse pas revenir en arrière
            return // On arrête l'exécution de onCreate ici
        }

        // 2. Lier le code au fichier XML (vérifiez le nom de votre layout)
        setContentView(R.layout.activity_register)

        // 4. Faire le lien avec les éléments du design XML
        emailField = findViewById(R.id.emailEditText)
        passwordField = findViewById(R.id.passwordEditText)
        signUpButton = findViewById(R.id.registerButton)

        // 5. Déclencher l'action au clic sur le bouton
        signUpButton.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Succès : l'utilisateur est créé et connecté
                        val user = auth.currentUser
                        Toast.makeText(baseContext, "Compte créé !", Toast.LENGTH_SHORT).show()
                        // Rediriger vers l'écran principal
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Échec : afficher le message d'erreur
                        Toast.makeText(baseContext, "Erreur : ${task.exception?.message}",
                            Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

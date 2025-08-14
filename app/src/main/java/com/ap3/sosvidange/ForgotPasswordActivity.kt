package com.ap3.sosvidange

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var inputEmailOrPhone: EditText
    private lateinit var sendButton: Button
    private lateinit var backArrow: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        // Ajustement automatique pour barres système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Références UI
        inputEmailOrPhone = findViewById(R.id.input_email_or_phone)
        sendButton = findViewById(R.id.send_button)
        backArrow = findViewById(R.id.back_arrow)

        // Bouton retour
        backArrow.setOnClickListener {
            finish()
        }

        // Bouton "Envoyer"
        sendButton.setOnClickListener {
            val input = inputEmailOrPhone.text.toString().trim()

            if (input.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer votre email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ici, Firebase ne prend que l'email pour reset password
            // Si tu veux gérer le téléphone, il faudrait un autre flux (SMS)
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                envoyerLienReinitialisation(input)
            } else {
                Toast.makeText(
                    this,
                    "Entrez une adresse email valide (ou gérez la version téléphone séparément)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun envoyerLienReinitialisation(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Un lien de réinitialisation a été envoyé à $email",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, ConnexionActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Erreur : ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}

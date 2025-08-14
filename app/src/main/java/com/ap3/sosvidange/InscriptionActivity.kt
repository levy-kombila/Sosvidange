package com.ap3.sosvidange

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class InscriptionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inscription)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        val fabRetour: FloatingActionButton = findViewById(R.id.fabRetour)
        val editNom: EditText = findViewById(R.id.editNom)
        val editStatut: EditText = findViewById(R.id.editStatut)
        val editNumero: EditText = findViewById(R.id.editNumero)
        val editEmail: EditText = findViewById(R.id.editEmail)
        val editPassword: EditText = findViewById(R.id.editPassword)
        val editPasswordConfirm: EditText = findViewById(R.id.editPasswordConfirm)
        val checkboxConditions: CheckBox = findViewById(R.id.checkboxConditions)
        val btnInscription: Button = findViewById(R.id.btnInscription)

        // Bouton retour
        fabRetour.setOnClickListener {
            startActivity(Intent(this, PortailActivity::class.java))
            finish()
        }

        btnInscription.setOnClickListener {
            val nom = editNom.text.toString().trim()
            val statut = editStatut.text.toString().trim()
            val numero = editNumero.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString()
            val passwordConfirm = editPasswordConfirm.text.toString()

            // Vérification des champs
            if (nom.isEmpty() || statut.isEmpty() || numero.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != passwordConfirm) {
                Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!checkboxConditions.isChecked) {
                Toast.makeText(this, "Veuillez accepter les conditions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Création de l'utilisateur Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        val userMap = mapOf(
                            "nom" to nom,
                            "statut" to statut,
                            "numero" to numero,
                            "email" to email
                        )

                        // Sauvegarde dans Realtime Database
                        if (userId != null) {
                            FirebaseDatabase.getInstance().getReference("Utilisateurs")
                                .child(userId)
                                .setValue(userMap)
                                .addOnCompleteListener { saveTask ->
                                    if (saveTask.isSuccessful) {
                                        Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, AccueilActivity::class.java))
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Erreur enregistrement: ${saveTask.exception?.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erreur: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

private lateinit var googleSignInClient: GoogleSignInClient
private val RC_SIGN_IN = 9001

class ConnexionActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_connexion)

        // Redirection auto si déjà connecté
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            startActivity(Intent(this, AccueilActivity::class.java))
            finish()
            return
        }

        // Ajuster les paddings pour les barres système
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val imageViewGoogle = findViewById<ImageView>(R.id.imageViewGoogle)
        imageViewGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        val emailEditText = findViewById<EditText>(R.id.editEmail)
        val passwordEditText = findViewById<EditText>(R.id.editPassword)
        val btnConnexion = findViewById<Button>(R.id.btnConnexion)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val createAccount2 = findViewById<TextView>(R.id.createAccount2)
        val fabAddSortir = findViewById<FloatingActionButton>(R.id.fabAddSortir)

        // Connexion email/mot de passe
        btnConnexion.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // On peut récupérer les infos dans Realtime Database
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                FirebaseDatabase.getInstance().getReference("Utilisateurs")
                                    .child(uid)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        val nom = snapshot.child("nom").value ?: ""
                                        Toast.makeText(this, "Bienvenue $nom", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, AccueilActivity::class.java))
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, AccueilActivity::class.java))
                                        finish()
                                    }
                            } else {
                                startActivity(Intent(this, AccueilActivity::class.java))
                                finish()
                            }
                        } else {
                            Toast.makeText(this, "Erreur : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        createAccount2.setOnClickListener {
            startActivity(Intent(this, InscriptionActivity::class.java))
        }

        fabAddSortir.setOnClickListener {
            startActivity(Intent(this, PortailActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Erreur connexion Google: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Connecté avec : ${user?.email}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AccueilActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Échec de l'authentification Google", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

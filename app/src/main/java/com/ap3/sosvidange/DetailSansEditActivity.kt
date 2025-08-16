package com.ap3.sosvidange

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import com.google.firebase.database.*

class DetailSansEditActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var signalementId: String? = null

    private lateinit var detailImage: ImageView
    private lateinit var detailVille: TextView
    private lateinit var detailQuartier: TextView
    private lateinit var detailCommentaire: TextView
    private lateinit var detailDateHeure: TextView
    private lateinit var detailEtat: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_signalement)

        // Récupère les vues de ton layout XML
        detailImage = findViewById(R.id.detailImage)
        detailVille = findViewById(R.id.detailVille)
        detailQuartier = findViewById(R.id.detailQuartier)
        detailCommentaire = findViewById(R.id.detailCommentaire)
        detailDateHeure = findViewById(R.id.detailDateHeure)
        detailEtat = findViewById(R.id.detailEtat)

        // Récupère l'ID passé depuis l'adapter
        signalementId = intent.getStringExtra("signalementId")

        if (signalementId == null) {
            Toast.makeText(this, "Erreur : ID manquant", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Référence Firebase
        databaseReference = FirebaseDatabase.getInstance()
            .getReference("Signalements")
            .child(signalementId!!)

        // Charger les données
        loadSignalement()
    }

    private fun loadSignalement() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sig = snapshot.getValue(Signalement::class.java)
                sig?.let {
                    detailVille.text = it.nomVille
                    detailQuartier.text = it.nomQuartier
                    detailCommentaire.text = it.commentaire
                    detailDateHeure.text = it.dateHeure
                    detailEtat.text = it.etat

                    // Charger l’image avec Coil
                    detailImage.load(it.image?.replace("http://", "https://"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@DetailSansEditActivity,
                    "Erreur : ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

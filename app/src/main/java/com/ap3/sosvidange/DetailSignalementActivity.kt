package com.ap3.sosvidange

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import com.google.firebase.database.*

class DetailSignalementActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private var signalementId: String? = null

    private lateinit var detailImage: ImageView
    private lateinit var detailVille: TextView
    private lateinit var detailQuartier: TextView
    private lateinit var detailCommentaire: TextView
    private lateinit var detailDate: TextView
    private lateinit var spinnerEtat: Spinner
    private lateinit var btnEnregistrer: Button

    private val etats = arrayOf("Urgent", "En cours", "Traité")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_signalement)

        // Init vues
        detailImage = findViewById(R.id.detailImage)
        detailVille = findViewById(R.id.detailVille)
        detailQuartier = findViewById(R.id.detailQuartier)
        detailCommentaire = findViewById(R.id.detailCommentaire)
        detailDate = findViewById(R.id.detailDate)
        spinnerEtat = findViewById(R.id.spinnerEtat)
        btnEnregistrer = findViewById(R.id.btnEnregistrer)

        // Remplir le Spinner avec les valeurs prédéfinies
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etats)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEtat.adapter = adapter

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

        // Sauvegarde modifications
        btnEnregistrer.setOnClickListener {
            val newEtat = spinnerEtat.selectedItem.toString()
            databaseReference.child("etat").setValue(newEtat)
                .addOnSuccessListener {
                    Toast.makeText(this, "État mis à jour", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur : ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadSignalement() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val sig = snapshot.getValue(Signalement::class.java)
                sig?.let {
                    detailVille.text = it.nomVille
                    detailQuartier.text = it.nomQuartier
                    detailCommentaire.text = it.commentaire
                    detailDate.text = it.dateHeure

                    // Charger l’image
                    detailImage.load(it.image?.replace("http://", "https://"))

                    // Sélectionner l'état actuel dans le Spinner
                    val index = etats.indexOf(it.etat)
                    if (index >= 0) spinnerEtat.setSelection(index)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailSignalementActivity, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

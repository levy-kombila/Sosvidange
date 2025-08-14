package com.ap3.sosvidange

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil3.load
import com.google.firebase.database.*

class DetailSignalementActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private var signalementId: String? = null

    // Views
    private lateinit var imgDetail: ImageView
    private lateinit var tvVille: TextView
    private lateinit var tvQuartier: TextView
    private lateinit var tvCommentaire: TextView
    private lateinit var tvDate: TextView
    private lateinit var editEtat: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_signalement)

        // findViewById (IDs selon ton layout)
        imgDetail = findViewById(R.id.detailImage)
        tvVille = findViewById(R.id.detailVille)
        tvQuartier = findViewById(R.id.detailQuartier)
        tvCommentaire = findViewById(R.id.detailCommentaire)
        tvDate = findViewById(R.id.detailDate)
        editEtat = findViewById(R.id.editEtat)
        btnSave = findViewById(R.id.btnEnregistrer)

        // Extras & ID
        signalementId = intent.getStringExtra("id")
        val extraImage = intent.getStringExtra("image")
        val extraType = intent.getStringExtra("type")
        val extraVille = intent.getStringExtra("nomVille")
        val extraQuartier = intent.getStringExtra("nomQuartier")
        val extraCommentaire = intent.getStringExtra("commentaire")
        val extraDate = intent.getStringExtra("dateHeure")
        val extraEtat = intent.getStringExtra("etat")

        // DB ref
        databaseRef = FirebaseDatabase.getInstance().getReference("Signalements")

        if (signalementId != null) {
            databaseRef.child(signalementId!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val s = snapshot.getValue(Signalement::class.java)
                    if (s != null) {
                        populateViewsFromSignalement(s)
                    } else {
                        populateViewsFromExtras(extraImage, extraType, extraVille, extraQuartier, extraCommentaire, extraDate, extraEtat)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DetailSignalementActivity, "Erreur de chargement", Toast.LENGTH_SHORT).show()
                    populateViewsFromExtras(extraImage, extraType, extraVille, extraQuartier, extraCommentaire, extraDate, extraEtat)
                }
            })
        } else {
            // Pas d'ID : fallback aux extras et désactive la sauvegarde
            populateViewsFromExtras(extraImage, extraType, extraVille, extraQuartier, extraCommentaire, extraDate, extraEtat)
            btnSave.isEnabled = false
            btnSave.alpha = 0.5f
        }

        // Sauvegarder nouvel état (si id présent)
        btnSave.setOnClickListener {
            val newEtat = editEtat.text.toString().trim()
            if (newEtat.isEmpty()) {
                Toast.makeText(this, "Renseigne un état", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (signalementId == null) {
                Toast.makeText(this, "Impossible de sauvegarder (ID manquant)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            databaseRef.child(signalementId!!).child("etat").setValue(newEtat)
                .addOnSuccessListener {
                    Toast.makeText(this, "État mis à jour", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun populateViewsFromSignalement(s: Signalement) {
        imgDetail.load(s.image?.replace("http://", "https://"))
        tvVille.text = s.nomVille ?: ""
        tvQuartier.text = s.nomQuartier ?: ""
        tvCommentaire.text = s.commentaire ?: ""
        tvDate.text = s.dateHeure ?: ""
        editEtat.setText(s.etat ?: "")
    }

    private fun populateViewsFromExtras(
        image: String?,
        type: String?,
        ville: String?,
        quartier: String?,
        commentaire: String?,
        dateHeure: String?,
        etat: String?
    ) {
        imgDetail.load(image?.replace("http://", "https://"))
        tvVille.text = ville ?: ""
        tvQuartier.text = quartier ?: ""
        tvCommentaire.text = commentaire ?: ""
        tvDate.text = dateHeure ?: ""
        editEtat.setText(etat ?: "")
    }
}

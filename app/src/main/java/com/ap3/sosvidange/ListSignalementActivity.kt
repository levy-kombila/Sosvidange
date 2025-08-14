package com.ap3.sosvidange

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class ListSignalementActivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var signalementArrayList: ArrayList<Signalement>
    private lateinit var adapter: SignalementAdapter
    private lateinit var searchInput: EditText
    private lateinit var iconSearch: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_signalement)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boutons flottants
        val fabAddSortir: FloatingActionButton = findViewById(R.id.fabAddSortir)
        fabAddSortir.setOnClickListener {
            startActivity(Intent(this, PortailActivity::class.java))
        }

        val fabAddSignalement: FloatingActionButton = findViewById(R.id.fabAddSignalement)
        fabAddSignalement.setOnClickListener {
            startActivity(Intent(this, SignalementActivity::class.java))
        }

        // Barre de recherche
        searchInput = findViewById(R.id.searchInput)
        iconSearch = findViewById(R.id.iconSearch)
        iconSearch.visibility = View.GONE

        // Effacer texte quand on clique sur la croix
        iconSearch.setOnClickListener { searchInput.text.clear() }

        // Liste + adapter
        signalementArrayList = arrayListOf()
        adapter = SignalementAdapter(signalementArrayList)
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewGallery)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Récupération Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Signalements")
        databaseReference.limitToFirst(100).addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    signalementArrayList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val sig = dataSnapshot.getValue(Signalement::class.java)
                        sig?.let { signalementArrayList.add(it) }
                    }
                    adapter.updateList(signalementArrayList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListSignalementActivity, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Filtre multiple
        val iconFilter: ImageView = findViewById(R.id.iconFilter)
        iconFilter.setOnClickListener {
            val options = arrayOf("Ville", "Quartier", "Jour", "Mois", "Heure")
            val selectedItems = ArrayList<Int>()

            AlertDialog.Builder(this)
                .setTitle("Choisissez les critères")
                .setMultiChoiceItems(options, null) { _, which, isChecked ->
                    if (isChecked) selectedItems.add(which) else selectedItems.remove(Integer.valueOf(which))
                }
                .setPositiveButton("Valider") { _, _ ->
                    Toast.makeText(this, "Filtres appliqués", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        // Recherche en temps réel
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                iconSearch.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                val text = s?.toString()?.trim() ?: ""
                if (text.isEmpty()) {
                    // Si recherche vide → liste complète
                    adapter.updateList(signalementArrayList)
                } else {
                    searchSignalements(text)
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Méthode de recherche
    private fun searchSignalements(query: String) {
        val keywords = query.split(",")
            .map { it.trim().lowercase() }
            .filter { it.isNotEmpty() }

        val filteredList = signalementArrayList.filter { sig ->
            val ville = sig.nomVille?.lowercase() ?: ""
            val quartier = sig.nomQuartier?.lowercase() ?: ""
            val dateHeure = sig.dateHeure?.lowercase() ?: ""
            val commentaire = sig.commentaire?.lowercase() ?: ""

            keywords.any { kw ->
                ville.contains(kw) ||
                        quartier.contains(kw) ||
                        dateHeure.contains(kw) ||
                        commentaire.contains(kw)
            }
        }

        adapter.updateList(filteredList)
    }
}

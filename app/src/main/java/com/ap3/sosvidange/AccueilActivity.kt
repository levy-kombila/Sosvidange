package com.ap3.sosvidange

import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.content.Intent

class AccueilActivity : AppCompatActivity() {

    private lateinit var nouveauAdapter: AccueilNouveauAdapter
    private lateinit var autreAdapter: AccueilAutreAdapter

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("Signalements")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)

        // Configuration de la barre de navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home
        setupBottomNavigation(this, bottomNav)

        val recyclerNouveau = findViewById<RecyclerView>(R.id.recyclerViewRecent)
        val recyclerAutre = findViewById<RecyclerView>(R.id.recyclerViewAll)

        recyclerNouveau.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerAutre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        nouveauAdapter = AccueilNouveauAdapter(emptyList())
        autreAdapter = AccueilAutreAdapter(emptyList())

        recyclerNouveau.adapter = nouveauAdapter
        recyclerAutre.adapter = autreAdapter

        chargerSignalements()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chargerSignalements() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allSignalements = mutableListOf<Signalement>()

                val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.FRANCE)

                // Récupère tous les signalements
                for (data in snapshot.children) {
                    val signalement = data.getValue(Signalement::class.java)
                    if (signalement != null) {
                        allSignalements.add(signalement)
                    }
                }

                // Trie par dateHeure décroissante (plus récent en premier)
                val sortedSignalements = allSignalements.sortedByDescending { s ->
                    try {
                        val dh = s.dateHeure ?: ""
                        LocalDateTime.parse(dh, formatter)
                    } catch (e: Exception) {
                        LocalDateTime.MIN
                    }
                }

                // Prend 3 plus récents pour recyclerViewRecent
                val topRecent = sortedSignalements.take(3)

                // Le reste va dans recyclerViewAll (sans les récents)
                val autres = sortedSignalements.drop(3)

                // Met à jour les adapters
                nouveauAdapter.updateList(topRecent)
                autreAdapter.updateList(autres)
            }

            override fun onCancelled(error: DatabaseError) {
                // Gérer erreur ici si besoin (Toast, Log...)
            }
        })
    }
}

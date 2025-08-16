package com.ap3.sosvidange

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class AccueilActivity : AppCompatActivity() {

    private lateinit var nouveauAdapter: AccueilNouveauAdapter
    private lateinit var autreAdapter: AccueilAutreAdapter

    private val database: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("Signalements")
    }

    private var allSignalements: List<Signalement> = emptyList()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accueil)

        // --- NAVIGATION ---
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_home
        setupBottomNavigation(this, bottomNav)

        // --- RECYCLER ---
        val recyclerNouveau = findViewById<RecyclerView>(R.id.recyclerViewRecent)
        val recyclerAutre = findViewById<RecyclerView>(R.id.recyclerViewAll)

        recyclerNouveau.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerAutre.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        nouveauAdapter = AccueilNouveauAdapter(emptyList())
        autreAdapter = AccueilAutreAdapter(emptyList())

        recyclerNouveau.adapter = nouveauAdapter
        recyclerAutre.adapter = autreAdapter

        // Charger signalements
        chargerSignalements()

        // --- FILTRES ---
        findViewById<TextView>(R.id.filterRecent).setOnClickListener { appliquerFiltre("RECENT") }
        findViewById<TextView>(R.id.filterTotal).setOnClickListener { appliquerFiltre("TOTAL") }
        findViewById<TextView>(R.id.filterUrgence).setOnClickListener { appliquerFiltre("URGENCE") }
        findViewById<TextView>(R.id.filterEnCours).setOnClickListener { appliquerFiltre("EN_COURS") }
        findViewById<TextView>(R.id.filterTraite).setOnClickListener { appliquerFiltre("TRAITE") }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun chargerSignalements() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<Signalement>()

                for (data in snapshot.children) {
                    val signalement = data.getValue(Signalement::class.java)
                    if (signalement != null) tempList.add(signalement)
                }

                allSignalements = tempList

                // Par défaut → RECENT
                appliquerFiltre("RECENT")

                // Les autres (non récents) dans le 2ème RecyclerView
                val autres = allSignalements.sortedByDescending { it.dateHeure }.drop(3)
                autreAdapter.updateList(autres)
            }

            override fun onCancelled(error: DatabaseError) {
                // gérer erreur
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun appliquerFiltre(type: String) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy", Locale.FRANCE)

        val filtered = when (type) {
            "RECENT" -> {
                allSignalements.sortedByDescending { s ->
                    try {
                        LocalDateTime.parse(s.dateHeure ?: "", formatter)
                    } catch (e: Exception) {
                        LocalDateTime.MIN
                    }
                }.take(5) // Les 5 plus récents
            }
            "TOTAL" -> allSignalements
            "URGENCE" -> allSignalements.filter { it.etat?.lowercase(Locale.ROOT) == "urgent" }
            "EN_COURS" -> allSignalements.filter { it.etat?.lowercase(Locale.ROOT) == "en cours" }
            "TRAITE" -> allSignalements.filter { it.etat?.lowercase(Locale.ROOT) == "traité" }
            else -> allSignalements
        }

        // On ne met à jour QUE le 1er RecyclerView
        nouveauAdapter.updateList(filtered)

        // Mettre à jour le texte et le nombre
        majTextFiltres(type, filtered.size)
    }
    private fun majTextFiltres(type: String, count: Int) {
        val textTitre = findViewById<TextView>(R.id.textViewNouveauSignalement)
        val textNombre = findViewById<TextView>(R.id.textNombre)

        // Changer le titre selon le filtre
        val titre = when (type) {
            "RECENT" -> "Signalements récents"
            "TOTAL" -> "Tous les signalements"
            "URGENCE" -> "Signalements urgents"
            "EN_COURS" -> "Signalements en cours"
            "TRAITE" -> "Signalements traités"
            else -> "Signalements"
        }

        textTitre.text = titre
        textNombre.text = count.toString()
    }

}


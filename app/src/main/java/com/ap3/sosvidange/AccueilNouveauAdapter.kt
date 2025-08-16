package com.ap3.sosvidange

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load

class AccueilNouveauAdapter(
    private var signalements: List<Signalement>
) : RecyclerView.Adapter<AccueilNouveauAdapter.NouveauViewHolder>() {

    class NouveauViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageSignalement)
        val titre: TextView = view.findViewById(R.id.tvTitre)
        val lieu: TextView = view.findViewById(R.id.tvLieu)
        val imageAngle: ImageView = view.findViewById(R.id.imageAngle)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NouveauViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_accueil_nouveau, parent, false)
        return NouveauViewHolder(view)
    }

    override fun onBindViewHolder(holder: NouveauViewHolder, position: Int) {
        val signalement = signalements[position]

        holder.imageView.load(signalement.image?.replace("http://", "https://"))
        holder.titre.text = signalement.etat ?: "Sans titre"
        holder.lieu.text = "${signalement.nomQuartier}, ${signalement.nomVille}"


        // Clic sur la flèche pour voir les détails
        holder.imageAngle.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailSansEditActivity::class.java)
            intent.putExtra("signalementId", signalement.id) // Passe l’ID Firebase
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = signalements.size

    fun updateList(newList: List<Signalement>) {
        signalements = newList
        notifyDataSetChanged()
    }
}

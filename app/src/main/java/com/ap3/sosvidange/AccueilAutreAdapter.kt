package com.ap3.sosvidange

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load

class AccueilAutreAdapter(
    private var signalements: List<Signalement>
) : RecyclerView.Adapter<AccueilAutreAdapter.AutreViewHolder>() {

    class AutreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageAutre)
        val titre: TextView = view.findViewById(R.id.tvTitreAutre)
        val lieu: TextView = view.findViewById(R.id.tvLieuAutre)
        val btnVoir: Button = view.findViewById(R.id.btnVoir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_accueil_autre, parent, false)
        return AutreViewHolder(view)
    }

    override fun onBindViewHolder(holder: AutreViewHolder, position: Int) {
        val signalement = signalements[position]

        holder.imageView.load(signalement.image?.replace("http://", "https://"))
        holder.titre.text = signalement.etat ?: "Sans titre" // Remplacer par l'etat qui est Urgent
        holder.lieu.text = "${signalement.nomQuartier}, ${signalement.nomVille}"

        holder.btnVoir.setOnClickListener {
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

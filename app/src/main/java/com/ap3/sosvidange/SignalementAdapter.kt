package com.ap3.sosvidange

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load

class SignalementAdapter(private var signalements: List<Signalement>) :
    RecyclerView.Adapter<SignalementAdapter.SignalementViewHolder>() {

    class SignalementViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageSignalement)
        val nomVille: TextView = view.findViewById(R.id.tvNomVille)
        val nomQuartier: TextView = view.findViewById(R.id.tvNomQuartier)
        val commentaire: TextView = view.findViewById(R.id.tvCommentaire)
        val dateHeure: TextView = view.findViewById(R.id.tvDateHeure)
        val etat: TextView = view.findViewById(R.id.tvEtat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.signalement_item, parent, false)
        return SignalementViewHolder(view)
    }

    override fun onBindViewHolder(holder: SignalementViewHolder, position: Int) {
        val signalement = signalements[position]

        holder.imageView.load(signalement.image?.replace("http://", "https://"))
        holder.nomVille.text = signalement.nomVille
        holder.nomQuartier.text = signalement.nomQuartier
        holder.commentaire.text = signalement.commentaire
        holder.dateHeure.text = signalement.dateHeure
        holder.etat.text = signalement.etat

        // Gestion couleur état
        when (signalement.etat) {
            "Urgent" -> holder.etat.setTextColor(android.graphics.Color.parseColor("#E53935"))
            "En cours" -> holder.etat.setTextColor(android.graphics.Color.parseColor("#FB8C00"))
            "Traité" -> holder.etat.setTextColor(android.graphics.Color.parseColor("#43A047"))
            else -> holder.etat.setTextColor(android.graphics.Color.parseColor("#E53935"))
        }
    }

    override fun getItemCount() = signalements.size

    fun updateList(newList: List<Signalement>) {
        signalements = newList
        notifyDataSetChanged()
    }
}

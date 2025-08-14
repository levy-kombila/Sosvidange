package com.ap3.sosvidange

import android.adservices.adid.AdId

data class Signalement(
    var id: String?=null,
    var image: String?=null,
    var nomVille: String?=null,
    var nomQuartier: String?=null,
    var commentaire: String?=null,
    var dateHeure: String?=null,
    var etat: String? = "Urgent" // Valeur par défaut
)

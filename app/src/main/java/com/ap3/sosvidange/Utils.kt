// Utils.kt
package com.ap3.sosvidange

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

fun setupBottomNavigation(activity: Activity, bottomNav: BottomNavigationView) {
    bottomNav.setOnItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                if (activity !is AccueilActivity) {
                    activity.startActivity(Intent(activity, AccueilActivity::class.java))
                    activity.overridePendingTransition(0, 0)
                }
                true
            }
            R.id.nav_action -> {
                if (activity !is ListeSignalementActionActivity) {
                    activity.startActivity(Intent(activity, ListeSignalementActionActivity::class.java))
                    activity.overridePendingTransition(0, 0)
                }
                true
            }
            R.id.nav_map -> {
                if (activity !is CarteActivity) {
                    activity.startActivity(Intent(activity, CarteActivity::class.java))
                    activity.overridePendingTransition(0, 0)
                }
                true
            }
            R.id.nav_settings -> {
                if (activity !is SettingsActivity) {
                    activity.startActivity(Intent(activity, SettingsActivity::class.java))
                    activity.overridePendingTransition(0, 0)
                }
                true
            }
            else -> false
        }
    }
}

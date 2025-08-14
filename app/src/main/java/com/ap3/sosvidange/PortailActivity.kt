package com.ap3.sosvidange

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PortailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_portail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnSignaler: Button = findViewById(R.id.btnSignaler)
        btnSignaler.setOnClickListener {
            val intent = Intent(this, SignalementActivity::class.java)
            startActivity(intent)
        }
        val btnlistSignalement: Button = findViewById(R.id.btnListSignalement)
        btnlistSignalement.setOnClickListener {
            val intent = Intent(this, ListSignalementActivity::class.java)
            startActivity(intent)
        }

        val btnConnexion: Button = findViewById(R.id.btnConnexion)
        btnConnexion.setOnClickListener {
            val intent = Intent(this, ConnexionActivity::class.java)
            startActivity(intent)
        }

    }
}
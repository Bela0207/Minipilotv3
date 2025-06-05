package com.isabela.minipilot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class viagem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viagem)

        val button = findViewById<Button>(R.id.language_button4)
        val button2 = findViewById<Button>(R.id.button5)
        val button3 = findViewById<Button>(R.id.button9)

        button.setOnClickListener {
            val intent = Intent(this, config::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(this, telainicial::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, estat::class.java)
            startActivity(intent)
        }
    }
}
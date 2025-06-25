package com.isabela.minipilot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class conectar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        val button = findViewById<Button>(R.id.language_button2)
        val button2 = findViewById<Button>(R.id.button15)
        val button3 = findViewById<Button>(R.id.button14)
        val button4 = findViewById<Button>(R.id.button16)

        button.setOnClickListener {
            val intent = Intent(this, config::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(this, estat::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, telainicial::class.java)
            startActivity(intent)
        }
        button4.setOnClickListener {
            val intent = Intent(this, viagem::class.java)
            startActivity(intent)
        }
    }
}
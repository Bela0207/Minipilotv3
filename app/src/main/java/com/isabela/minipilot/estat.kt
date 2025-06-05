package com.isabela.minipilot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class estat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estat)


        val button = findViewById<Button>(R.id.button4)
        val button2 = findViewById<Button>(R.id.button11)
        val button3 = findViewById<Button>(R.id.language_button3)

        button.setOnClickListener {
            val intent = Intent(this, telainicial::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(this, viagem::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, config::class.java)
            startActivity(intent)
        }
    }
}
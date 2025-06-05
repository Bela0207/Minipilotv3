package com.isabela.minipilot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.content.Intent



class telainicial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_telainicial)

        val button = findViewById<Button>(R.id.language_button)
        val button2 = findViewById<Button>(R.id.button3)
        val button3 = findViewById<Button>(R.id.button2)

        button.setOnClickListener {
            val intent = Intent(this, config::class.java)
            startActivity(intent)
        }
        button2.setOnClickListener {
            val intent = Intent(this, viagem::class.java)
            startActivity(intent)
        }
        button3.setOnClickListener {
            val intent = Intent(this, estat::class.java)
            startActivity(intent)
        }
    }
}
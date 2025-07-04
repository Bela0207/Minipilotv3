package com.isabela.minipilot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader

class estat : AppCompatActivity() {

    private lateinit var texta: TextView
    private lateinit var textv: TextView
    @Volatile
    private var isReading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estat)

        val button = findViewById<Button>(R.id.button4)
        val button2 = findViewById<Button>(R.id.button11)
        val button3 = findViewById<Button>(R.id.language_button3)

        button.setOnClickListener {
            startActivity(Intent(this, telainicial::class.java))
            stopReading()
        }
        button2.setOnClickListener {
            startActivity(Intent(this, viagem::class.java))
            stopReading()
        }
        button3.setOnClickListener {
            startActivity(Intent(this, config::class.java))
            stopReading()
        }

        texta = findViewById(R.id.texta)
        textv = findViewById(R.id.textv)

        val socket = MyBluetoothSocketHolder.socket
        Log.d("estat", "Socket null? ${socket == null}, conectado? ${socket?.isConnected}")

        if (socket != null && socket.isConnected) {
            val reader = BufferedReader(InputStreamReader(socket.inputStream))
            lerDadosESP(reader)
        } else {
            texta.text = "Não conectado"
            textv.text = "Não conectado"
        }
    }

    private fun lerDadosESP(reader: BufferedReader) {
        Thread {
            try {
                while (isReading) {
                    val linha = reader.readLine() ?: break
                    Log.d("estat", "Linha recebida: $linha")

                    when {
                        linha.contains("Acel:") -> {
                            val valor = linha.substringAfter("Acel:").trim()
                            runOnUiThread {
                                texta.text = "Aceleração: $valor"
                            }
                        }
                        linha.contains("Velocidade:") -> {
                            val valor = linha.substringAfter("Velocidade:").trim()
                            runOnUiThread {
                                textv.text = "Velocidade Média: $valor"
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("estat", "Erro na leitura: ${e.message}")
            } finally {
                reader.close()
                Log.d("estat", "Leitura finalizada e reader fechado.")
            }
        }.start()
    }

    private fun stopReading() {
        isReading = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopReading()
    }
}

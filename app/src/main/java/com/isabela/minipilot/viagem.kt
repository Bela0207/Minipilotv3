package com.isabela.minipilot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.OutputStream

class viagem : AppCompatActivity() {

    private var outputStream: OutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viagem)

        val buttonConfig = findViewById<Button>(R.id.language_button4)
        val buttonInicio = findViewById<Button>(R.id.button5)
        val buttonEstat = findViewById<Button>(R.id.button9)
        val buttonIniciar = findViewById<Button>(R.id.buttonIniciar)
        val buttonFinalizar = findViewById<Button>(R.id.buttonFinalizar)

        buttonConfig.setOnClickListener {
            startActivity(Intent(this, config::class.java))
        }
        buttonInicio.setOnClickListener {
            startActivity(Intent(this, telainicial::class.java))
        }
        buttonEstat.setOnClickListener {
            startActivity(Intent(this, estat::class.java))
        }

        val socket = MyBluetoothSocketHolder.socket
        if (socket != null && socket.isConnected) {
            outputStream = socket.outputStream
            Log.d("Viagem", "OutputStream inicializado")
        } else {
            Toast.makeText(this, "Bluetooth não conectado", Toast.LENGTH_SHORT).show()
            Log.e("Viagem", "Socket desconectado ou nulo")
        }

        buttonIniciar.setOnClickListener {
            enviarComando('i')
        }

        buttonFinalizar.setOnClickListener {
            enviarComando('f')
        }
    }

    private fun enviarComando(comando: Char) {
        try {
            if (outputStream != null) {
                outputStream!!.write(comando.code)
                outputStream!!.flush()
                Toast.makeText(this, "Comando '$comando' enviado", Toast.LENGTH_SHORT).show()
                Log.d("Viagem", "Comando enviado: $comando")
            } else {
                Toast.makeText(this, "Sem conexão Bluetooth", Toast.LENGTH_SHORT).show()
                Log.e("Viagem", "OutputStream é null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao enviar comando", Toast.LENGTH_SHORT).show()
            Log.e("Viagem", "Erro no envio: ${e.message}")
        }
    }
}

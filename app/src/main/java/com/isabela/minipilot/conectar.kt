package com.isabela.minipilot

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class conectar : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        // Botões de navegação
        val button = findViewById<Button>(R.id.language_button2)
        val button2 = findViewById<Button>(R.id.button15)
        val button3 = findViewById<Button>(R.id.button14)
        val button4 = findViewById<Button>(R.id.button16)

        button.setOnClickListener {
            startActivity(Intent(this, config::class.java))
        }
        button2.setOnClickListener {
            startActivity(Intent(this, estat::class.java))
        }
        button3.setOnClickListener {
            startActivity(Intent(this, telainicial::class.java))
        }
        button4.setOnClickListener {
            startActivity(Intent(this, viagem::class.java))
        }

        // Configura ListView para mostrar dispositivos
        listView = findViewById(R.id.listViewBluetooth)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        checkPermissions()

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (!bluetoothAdapter.isEnabled) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableIntent, 1)
            } else {
                iniciarBuscaBluetooth()
            }
        } else {
            checkPermissions()
        }
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val notGranted = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), 1)
        }
    }

    private fun iniciarBuscaBluetooth() {
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                    try {
                        if (ActivityCompat.checkSelfPermission(
                                this@conectar,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            val device: BluetoothDevice? =
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                            val nome = device?.name ?: "Sem nome"
                            val endereco = device?.address ?: "Sem endereço"
                            adapter.add("$nome ($endereco)")
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bluetoothAdapter.startDiscovery()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::receiver.isInitialized) {
            unregisterReceiver(receiver)
        }
    }
}

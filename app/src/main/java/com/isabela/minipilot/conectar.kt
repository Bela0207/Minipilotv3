package com.isabela.minipilot

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class conectar : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var receiver: BroadcastReceiver
    private val encontrados = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        listView = findViewById(R.id.listViewBluetooth)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        // Botões
        findViewById<Button>(R.id.language_button2).setOnClickListener {
            startActivity(Intent(this, config::class.java))
        }
        findViewById<Button>(R.id.button15).setOnClickListener {
            startActivity(Intent(this, estat::class.java))
        }
        findViewById<Button>(R.id.button14).setOnClickListener {
            startActivity(Intent(this, telainicial::class.java))
        }
        findViewById<Button>(R.id.button16).setOnClickListener {
            startActivity(Intent(this, viagem::class.java))
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        val notGranted = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), 1001)
        } else {
            iniciarBluetooth()
        }
    }

    private fun iniciarBluetooth() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, 1)
        } else {
            iniciarBusca()
        }
    }

    private fun iniciarBusca() {
        encontrados.clear()
        adapter.clear()

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val name = device?.name
                    val address = device?.address ?: "Sem endereço"

                    if (!name.isNullOrBlank()) {
                        val display = "$name ($address)"
                        if (encontrados.add(display)) {
                            adapter.add(display)
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            == PackageManager.PERMISSION_GRANTED
        ) {
            bluetoothAdapter.startDiscovery()
            Toast.makeText(this, "Buscando dispositivos...", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1001 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            iniciarBluetooth()
        } else {
            Toast.makeText(this, "Permissões de Bluetooth negadas", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::receiver.isInitialized) {
            unregisterReceiver(receiver)
        }
    }
}

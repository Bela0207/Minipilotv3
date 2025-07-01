package com.isabela.minipilot

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

private lateinit var bluetoothAdapter: BluetoothAdapter

class conectar : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)


        // Botões de navegação
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


        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não é suportado neste dispositivo", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1001
            )
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        val listView = findViewById<ListView>(R.id.listViewBluetooth)
        val pairedDevices = bluetoothAdapter.bondedDevices
        val deviceList = ArrayList<String>()

        for (device in pairedDevices) {
            val deviceName = device.name
            val deviceAddress = device.address // MAC address
            deviceList.add("$deviceName\n$deviceAddress")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView.adapter = adapter
    }
}
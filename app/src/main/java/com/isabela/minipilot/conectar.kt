package com.isabela.minipilot

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class conectar : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var deviceList: List<BluetoothDevice>
    private lateinit var listView: ListView

    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val permissions = arrayOf(
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_SCAN
    )
    private val REQUEST_CODE_PERMISSIONS = 101

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        listView = findViewById(R.id.listViewBluetooth)

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
        } else {
            setupBluetooth()
        }
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                setupBluetooth()
            } else {
                Toast.makeText(this, "Permissões Bluetooth negadas", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
    private fun setupBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled) {
            Toast.makeText(this, "Bluetooth não disponível ou desligado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        deviceList = pairedDevices.toList()

        val deviceNames = deviceList.map { it.name }
        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceNames)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val device = deviceList[position]
            ConnectThread(device).start()
        }
    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {
        @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN])
        override fun run() {
            var socket: BluetoothSocket? = null

            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID)
                bluetoothAdapter.cancelDiscovery()
                socket.connect()

                MyBluetoothSocketHolder.socket = socket

                runOnUiThread {
                    Toast.makeText(this@conectar, "Conectado com sucesso!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@conectar, "Erro na conexão: ${e.message}", Toast.LENGTH_LONG).show()
                }
                try {
                    socket?.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }
}

package com.isabela.minipilot

import android.Manifest
import android.bluetooth.*
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.IOException
import java.util.*

class conectar : AppCompatActivity() {

    private val REQUEST_CODE_BLUETOOTH_PERMISSIONS = 1001
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private val deviceList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    if (context == null) return
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return

                    val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    device?.let {
                        val info = "${it.name ?: "Desconhecido"}\n${it.address}"
                        if (!deviceList.contains(info)) {
                            deviceList.add(info)
                            runOnUiThread { adapter.notifyDataSetChanged() }
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    runOnUiThread {
                        Toast.makeText(this@conectar, "Scan finalizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        findViewById<Button>(R.id.button2).setOnClickListener {
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

        listView = findViewById(R.id.listViewBluetooth)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão BLUETOOTH_CONNECT necessária", Toast.LENGTH_SHORT).show()
                return@setOnItemClickListener
            }
            val info = deviceList[position]
            val address = info.substringAfter("\n")
            val device = bluetoothAdapter.getRemoteDevice(address)
            Toast.makeText(this, "Conectando a ${device.name ?: "desconhecido"}...", Toast.LENGTH_SHORT).show()
            ConnectThread(device).start()
        }

        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth não é suportado neste dispositivo", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                REQUEST_CODE_BLUETOOTH_PERMISSIONS
            )
        } else {
            startBluetoothProcess()
        }
    }

    private fun startBluetoothProcess() {
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                loadPairedDevices()
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                startDiscovery()
            }
        }
    }

    private fun loadPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) return

        try {
            val pairedDevices = bluetoothAdapter.bondedDevices
            for (device in pairedDevices) {
                val info = "${device.name ?: "Desconhecido"}\n${device.address}"
                if (!deviceList.contains(info)) deviceList.add(info)
            }
            adapter.notifyDataSetChanged()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erro ao acessar dispositivos pareados: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) return

        try {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_FOUND)
                addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            }
            registerReceiver(receiver, filter)

            if (bluetoothAdapter.isDiscovering) bluetoothAdapter.cancelDiscovery()
            bluetoothAdapter.startDiscovery()

            Toast.makeText(this, "Iniciando scan de dispositivos...", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Erro ao iniciar scan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_BLUETOOTH_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startBluetoothProcess()
            } else {
                Toast.makeText(this, "Permissões Bluetooth necessárias não concedidas", Toast.LENGTH_LONG).show()
                val intent = Intent(this, telainicial::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            // Receiver já estava desregistrado
        }
    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {
        private var socket: BluetoothSocket? = null

        override fun run() {
            if (ActivityCompat.checkSelfPermission(this@conectar, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                runOnUiThread {
                    Toast.makeText(this@conectar, "Permissão BLUETOOTH_CONNECT necessária para conectar", Toast.LENGTH_SHORT).show()
                }
                return
            }

            bluetoothAdapter.cancelDiscovery()

            try {
                // Tenta conexão padrão
                socket = device.createRfcommSocketToServiceRecord(MY_UUID)
                socket?.connect()
            } catch (e: IOException) {
                // Fallback via reflexão
                try {
                    val method = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                    socket = method.invoke(device, 1) as BluetoothSocket
                    socket?.connect()
                } catch (e2: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@conectar, "Falha na conexão: ${e2.message}", Toast.LENGTH_SHORT).show()
                    }
                    try {
                        socket?.close()
                    } catch (_: IOException) {}
                    return
                }
            }

            runOnUiThread {
                Toast.makeText(this@conectar, "Conectado a ${device.name ?: "desconhecido"}", Toast.LENGTH_SHORT).show()
            }

            // Aqui você pode iniciar InputStream / OutputStream
        }

        fun cancel() {
            try {
                socket?.close()
            } catch (_: IOException) {}
        }
    }
}

package com.isabela.minipilot

import android.Manifest
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

class conectar : AppCompatActivity() {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var receiver: BroadcastReceiver
    private val dispositivosEncontrados = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conectar)

        // Configura botões (ajuste conforme seu app)
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

        listView = findViewById(R.id.listViewBluetooth)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        checkPermissions()
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
            Log.d("Bluetooth", "Solicitando permissões: $notGranted")
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), 1)
        } else {
            Log.d("Bluetooth", "Permissões já concedidas")
            iniciarBluetooth()
        }
    }

    private fun iniciarBluetooth() {
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        Log.d("Bluetooth", "Bluetooth Adapter inicializado: $bluetoothAdapter")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Bluetooth", "Permissão BLUETOOTH_CONNECT não concedida")
            Toast.makeText(this, "Permissão Bluetooth negada", Toast.LENGTH_SHORT).show()
            return
        }

        if (!bluetoothAdapter.isEnabled) {
            Log.d("Bluetooth", "Bluetooth não ativado, pedindo ativação")
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableIntent, 1)
        } else {
            Log.d("Bluetooth", "Bluetooth ativado, iniciando busca")
            iniciarBuscaBluetooth()
        }
    }

    private fun iniciarBuscaBluetooth() {
        adapter.clear()
        dispositivosEncontrados.clear()

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == BluetoothDevice.ACTION_FOUND) {
                    if (ActivityCompat.checkSelfPermission(
                            this@conectar,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

                        val nomeRaw = device?.name
                        val endereco = device?.address ?: "Sem endereço"
                        val nome = if (nomeRaw.isNullOrBlank()) "Sem nome" else nomeRaw
                        val item = "$nome ($endereco)"

                        if (!dispositivosEncontrados.contains(item)) {
                            dispositivosEncontrados.add(item)
                            adapter.add(item)
                            Log.d("Bluetooth", "Detectado dispositivo: $item")
                        }
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("Bluetooth", "Permissão BLUETOOTH_SCAN não concedida")
            Toast.makeText(this, "Permissão BLUETOOTH_SCAN negada", Toast.LENGTH_SHORT).show()
            return
        }

        val iniciado = bluetoothAdapter.startDiscovery()
        Log.d("Bluetooth", "Iniciado o discovery: $iniciado")
        if (iniciado) {
            Toast.makeText(this, "Buscando dispositivos Bluetooth...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Falha ao iniciar a busca Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            Log.d("Bluetooth", "Permissões concedidas pelo usuário")
            iniciarBluetooth()
        } else {
            Log.d("Bluetooth", "Permissões negadas pelo usuário")
            Toast.makeText(this, "Permissões de Bluetooth negadas.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::receiver.isInitialized) {
            unregisterReceiver(receiver)
        }
    }
}

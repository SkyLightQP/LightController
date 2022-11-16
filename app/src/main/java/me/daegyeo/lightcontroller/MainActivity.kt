package me.daegyeo.lightcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.daegyeo.lightcontroller.databinding.ActivityMainBinding
import me.daegyeo.lightcontroller.permissions.Permission
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val permissionList = arrayOf(Permission(this, Manifest.permission.BLUETOOTH_CONNECT))

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        bluetoothAdapter = getSystemService(BluetoothManager::class.java).adapter
        setContentView(binding.root)
        setSupportActionBar(findViewById<Toolbar>(R.id.new_toolbar))

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        permissionList.forEach {
            if (!it.isGrant()) {
                Toast.makeText(this, "블루투스 사용을 위해 권한을 허용해주세요.", Toast.LENGTH_LONG).show()
                Permission.requestPermissions(this, arrayOf(it.permission), 1000)
            }
        }

        findViewById<ImageButton>(R.id.bluetooth).setOnClickListener {
            if (!bluetoothAdapter.isEnabled) {
                val bluetoothOnIndent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(bluetoothOnIndent)
            }
            showBluetoothDeviceDialog()
        }
    }

    private fun showBluetoothDeviceDialog() {
        val pairedDevices = bluetoothAdapter.bondedDevices
        if (pairedDevices.size > 0) {
            MaterialAlertDialogBuilder(this).apply {
                setTitle("연결할 전구를 선택해주세요.")
                val deviceNames = pairedDevices.map {
                    it.name
                }
                setItems(deviceNames.toTypedArray()) { _, which ->
                    connectBluetoothDevice(deviceNames[which])
                }
                create().show()
            }
        }
    }

    private fun connectBluetoothDevice(deviceName: String) {
        try {
            val device = bluetoothAdapter.bondedDevices.filter { it.name == deviceName }
            val socket =
                device[0].createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"))
            socket.connect()
        } catch (ex: IOException) {
            Log.e("TEST", ex.stackTraceToString())
            Toast.makeText(this, "${deviceName}에 연결을 할 수 없습니다.\n다시 시도해주세요.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
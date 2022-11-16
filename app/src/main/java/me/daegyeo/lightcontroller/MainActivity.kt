package me.daegyeo.lightcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import me.daegyeo.lightcontroller.databinding.ActivityMainBinding
import me.daegyeo.movingumbrella.runtimePermission.Permission
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val bluetoothConnect = Permission(this, Manifest.permission.BLUETOOTH_CONNECT)

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.new_toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        if (!bluetoothConnect.isGrant()) {
            Toast.makeText(this, "블루투스 사용을 위해 권한 허용을 해주세요.", Toast.LENGTH_LONG).show()
            Permission.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                1000
            )
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        findViewById<ImageButton>(R.id.bluetooth).setOnClickListener {
            if (!bluetoothAdapter.isEnabled) return@setOnClickListener

            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.size > 0) {
                AlertDialog.Builder(this).apply {
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
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
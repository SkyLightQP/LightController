package me.daegyeo.lightcontroller

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.switchmaterial.SwitchMaterial
import me.daegyeo.lightcontroller.databinding.ActivityMainBinding
import me.daegyeo.lightcontroller.permissions.Permission
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothThread: BluetoothThread? = null
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

        findViewById<Button>(R.id.frontButton1).setOnClickListener { sendData(byteArrayOf(DataPacket.FRONT1)) }
        findViewById<Button>(R.id.backButton1).setOnClickListener { sendData(byteArrayOf(DataPacket.BACK1)) }
        findViewById<Button>(R.id.frontButton2).setOnClickListener { sendData(byteArrayOf(DataPacket.FRONT2)) }
        findViewById<Button>(R.id.backButton2).setOnClickListener { sendData(byteArrayOf(DataPacket.BACK2)) }
        findViewById<Button>(R.id.leftButton).setOnClickListener { sendData(byteArrayOf(DataPacket.LEFT)) }
        findViewById<Button>(R.id.rightButton).setOnClickListener { sendData(byteArrayOf(DataPacket.RIGHT)) }
        findViewById<SwitchMaterial>(R.id.lightPower).setOnClickListener {
            if (it.isEnabled) sendData(byteArrayOf(DataPacket.LIGHT_ON))
            else sendData(byteArrayOf(DataPacket.LIGHT_OFF))
        }
        findViewById<CheckBox>(R.id.lightBrightness).setOnClickListener {
            if (it.isEnabled) sendData(byteArrayOf(DataPacket.LIGHT_AUTO_ON))
            else sendData(byteArrayOf(DataPacket.LIGHT_AUTO_OFF))
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
                    try {
                        bluetoothThread = connectBluetoothDevice(deviceNames[which])
                    } catch (ex: IOException) {
                        Toast.makeText(context, "${deviceNames[which]}에 연결을 할 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    } catch (ex: Error) {
                        Toast.makeText(context, ex.message, Toast.LENGTH_SHORT).show()
                    }
                }
                create().show()
            }
        }
    }

    private fun connectBluetoothDevice(deviceName: String): BluetoothThread {
        val device = bluetoothAdapter.bondedDevices.filter { it.name == deviceName }
        val socket =
            device[0].createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"))
        socket.connect()
        return BluetoothThread(socket)
    }

    private fun sendData(bytes: ByteArray) {
        if (bluetoothThread == null) {
            Toast.makeText(this, "블루투스를 연결해주세요.", Toast.LENGTH_LONG).show()
            return
        }
        bluetoothThread!!.write(bytes)
    }
}
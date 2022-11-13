package me.daegyeo.lightcontroller.receivers

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BluetoothReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val device: BluetoothDevice? = intent!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        val name = device!!.name
        Log.d("BT Name", name)
    }
}

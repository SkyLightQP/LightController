package me.daegyeo.lightcontroller

import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


class BluetoothThread(private val socket: BluetoothSocket) : Thread() {
    private var inputStream: InputStream
    private var outputStream: OutputStream

    init {
        try {
            inputStream = socket.inputStream
            outputStream = socket.outputStream
        } catch (e: IOException) {
            throw Error("블루투스 연결을 실패했습니다.")
        }
    }

    override fun run() {
        val buffer = ByteArray(1024)
        var bytes: Int
        while (true) {
            try {
                bytes = inputStream.read(buffer)
            } catch (e: IOException) {
                throw Error("블루투스 연결을 실패했습니다.")
                break
            }
        }
    }

    fun write(str: String) {
        val bytes = str.toByteArray()
        try {
            outputStream.write(bytes)
        } catch (e: IOException) {
            throw Error("전구 제어에 실패했습니다.")
        }
    }

    fun write(bytes: ByteArray) {
        try {
            outputStream.write(bytes)
        } catch (e: IOException) {
            throw Error("전구 제어에 실패했습니다.")
        }
    }

    fun cancel() {
        socket.close()
    }
}
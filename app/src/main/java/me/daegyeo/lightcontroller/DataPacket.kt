package me.daegyeo.lightcontroller

object DataPacket {
    val LIGHT_ON = 'O'.code.toByte()
    val LIGHT_OFF = 'F'.code.toByte()
    val LIGHT_AUTO_ON = 'A'.code.toByte()
    val LIGHT_AUTO_OFF = 'B'.code.toByte()
    val FRONT = 'F'.code.toByte()
    val BACK = 'B'.code.toByte()
    val LEFT = 'L'.code.toByte()
    val RIGHT = 'R'.code.toByte()
}
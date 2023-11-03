package com.example.ledpanelcontroller.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import okhttp3.internal.wait
import java.io.IOException
import java.util.UUID

object BluetoothHandler {

    private val PANEL_NAME = "rpi-led-panel"
    private val PANEL_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mSocket: BluetoothSocket? = null
    private lateinit var mReadThread: ReadThread

    @SuppressLint("MissingPermission")
    fun connect(btAdapter: BluetoothAdapter): Boolean {
        val pairedDevices: Set<BluetoothDevice>? = btAdapter.bondedDevices
        pairedDevices?.forEach { device ->
            if (device.name == PANEL_NAME) {

                mSocket = device.createRfcommSocketToServiceRecord(PANEL_UUID)
                if (mSocket != null) {
                    mSocket?.connect()
//                TODO: Support communication matrix -> app
//                mReadThread = ReadThread(mSocket!!)
//                mReadThread.start()
                    return true
                }
            }
        }
        return false
    }

    fun disconnect() {
        if (mSocket?.isConnected == true) {
            mSocket?.close()
        }
//        mReadThread.join()
    }

    fun isMatrixConnected() : Boolean {
        return mSocket?.isConnected ?: false
    }

    fun write(text: String): Boolean {
        // TODO: remove workaround when transferring issues fixed
        val msg = "              $text| ";
        val bytes: ByteArray = msg.toByteArray()
        if (mSocket?.isConnected == true) {
            mSocket?.outputStream?.write(bytes)
        }
        return true
    }

    private class ReadThread(private val mmSocket: BluetoothSocket) : Thread() {

        private val mmBuffer: ByteArray = ByteArray(1024)

        override fun run() {
            var numBytes: Int

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                numBytes = try {
                    mmSocket.inputStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d("DEBUG", "Exception in ReadThread: ${e.message}")
                    break
                }
                val message = String(mmBuffer.copyOfRange(0, numBytes))
                Log.d("DEBUG", "Received: $message")
            }
        }
    }
}
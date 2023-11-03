package com.example.ledpanelcontroller.mode

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.ledpanelcontroller.bluetooth.BluetoothHandler
import com.google.gson.Gson

class Message {
    private val mData : HashMap<String, Any> = HashMap()

    fun addModeName(name : String) : Message {
        mData[KEY_LEDMODE] = name
        return this
    }

    fun setPixel(x : Int, y : Int, color : Color) : Message {
        val pxl = Pixel(x, y, addColor(color));
        mData[KEY_SETPIXEL] = pxl
        return this
    }

    fun setFill(color : Color) : Message {
        mData[KEY_FILLMATRIX] = addColor(color)
        return this
    }

    fun modeChanged(newMode : Modes) : Message {
        mData[KEY_NEW_MODE] = newMode.name
        return this
    }

    fun addParameter(name : String, value : String) : Message {
        mData[name] = value
        return this
    }

    fun send() {
        val gson = Gson()
        val str = gson.toJson(mData)
        Log.d("DEBUG", "Sending message: $str")
        BluetoothHandler.write(str)
    }

    companion object {
        private const val KEY_LEDMODE = "name"
        private const val KEY_SETPIXEL = "set_pixel"
        private const val KEY_FILLMATRIX = "fill_matrix"
        private const val KEY_NEW_MODE = "new_mode"

        fun addColor(color : Color) : String {
            return Integer.toHexString(color.toArgb())
                .drop(2)  // remove Alpha-channel since it is not used
        }
    }

    class Pixel (val x : Int, val y : Int, val color : String) {

    }
}

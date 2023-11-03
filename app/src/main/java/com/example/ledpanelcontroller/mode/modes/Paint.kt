package com.example.ledpanelcontroller.mode.modes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ledpanelcontroller.ColorsDialog
import com.example.ledpanelcontroller.matrix.IMatrixUISubscriber
import com.example.ledpanelcontroller.matrix.Matrix
import com.example.ledpanelcontroller.matrix.MatrixUI
import com.example.ledpanelcontroller.mode.Message
import com.example.ledpanelcontroller.mode.Modes

object Paint : IMode {
    private var mActiveColor = Color.Green;
    private val mMatrixUiEventListener = object : IMatrixUISubscriber {
        override fun onTouch(x: Int, y: Int) {
            sendSetPixel(x, y)
            Matrix.setPixel(x, y, mActiveColor)
        }
    }

    @Composable
    override fun LedModeSettings(modifier: Modifier) {
        Row(modifier = modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            val chooseActiveColor = remember { mutableStateOf(false)}
            Button(modifier = Modifier.weight(1f),
                onClick = {
                chooseActiveColor.value = true
            }
            ) {
                Text("Active Color")
                if(chooseActiveColor.value) {
                    ColorsDialog({
                        chooseActiveColor.value = false
                    }
                    ) {
                        mActiveColor = it
                    }
                }
            }
            Button(modifier = Modifier.weight(1f),
                onClick = { sendClean() }) {
                Text(text = "Clear Matrix")
            }
        }
    }

    private fun sendSetPixel(x : Int, y : Int) {
        Message()
            .addModeName(Modes.PAINT.name)
            .setPixel(x, y, mActiveColor)
            .send()
    }

    private fun sendClean() {
        Matrix.clear()
        Message()
            .addModeName(Modes.PAINT.name)
            .setFill(Color.Black)
            .send()
    }

    override fun onModeActivated() {
        MatrixUI.subscribeOnEvents(mMatrixUiEventListener)
    }

    override fun onModeDeactivated() {
        MatrixUI.unsubscribeFromEvents(mMatrixUiEventListener)
        Matrix.clear()
    }
}
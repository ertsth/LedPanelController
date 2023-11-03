package com.example.ledpanelcontroller.mode.modes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ledpanelcontroller.mode.Message
import com.example.ledpanelcontroller.mode.Modes

object Custom : IMode {
    private val mScriptExample : String = """
    #matrix_fill(255, 0, 255)
    #matrix_set_pixel(20, 20, 255, 255, 0)
    #os.execute("sleep 3")
    #matrix_set_pixel(20, 20, 255, 0, 0)
    #os.execute("sleep 2")
    #matrix_clear()
    #os.execute("sleep 3")
    #matrix_fill(0, 255, 0)
    #os.execute("sleep 3")
    #matrix_clear()
    """.trimMargin("#")
    var mScript = mutableStateOf(mScriptExample)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun LedModeSettings(modifier: Modifier) {
        val text = remember { mScript }
        Column(modifier = modifier) {
            TextField(value = text.value, onValueChange = {
                mScript.value = it
            }, modifier = Modifier.fillMaxWidth().height(200.dp))
            Button(onClick = { sendMessage()}, modifier = Modifier.fillMaxWidth()) {
                Text("Submit")
            }
        }
    }

    private fun sendMessage() {
        Message()
            .addModeName(Modes.CUSTOM.name)
            .addParameter("script", mScript.value)
            .send()
    }
}